package com.example.coderyogui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class EventListener implements Listener {
    private final CoderyoGUI plugin;

    public EventListener(CoderyoGUI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryHolder holder = event.getInventory().getHolder();
        String title = event.getView().getTitle();
        int slot = event.getSlot();
        int rawSlot = event.getRawSlot();

        // 僅處理頂部庫存的點擊（忽略玩家背包）
        if (rawSlot != slot || rawSlot >= event.getInventory().getSize()) {
            return;
        }

        if (holder instanceof GUIHolder guiHolder) {
            CustomGUI gui = guiHolder.getGUI();
            GUIPage page = gui.pages().get(guiHolder.getPageId());
            GUIItem item = page.items().get(slot);
            if (page.allowInteract()) {
                event.setCancelled(false); // 允許拿取或存放
                if (item != null && !item.actions().isEmpty()) {
                    item.actions().forEach(action -> action.execute(player)); // 執行動作
                }
            } else {
                event.setCancelled(true);
                if (item != null && !item.actions().isEmpty()) {
                    item.actions().forEach(action -> action.execute(player)); // 執行動作
                }
            }
        } else if (holder instanceof EditorHolder editorHolder) {
            CustomGUI gui = editorHolder.getGUI();
            int pageId = editorHolder.getPageId();
            GUIPage page = gui.pages().get(pageId);

            if (title.startsWith("編輯: ")) {
                event.setCancelled(true);
                if (slot == 0) {
                    plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_name", -1, pageId));
                    player.closeInventory();
                    player.sendMessage("§a請輸入 GUI 名稱（輸入 /coderyogui cancel 取消）");
                } else if (slot == 1) {
                    GUIEditor.openRowSelect(player, gui, pageId);
                } else if (slot == 2) {
                    int newPageId = gui.pages().size() + 1;
                    gui.pages().put(newPageId, new GUIPage());
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, gui, newPageId);
                } else if (slot == 3) {
                    if (gui.pages().size() > 1) {
                        gui.pages().remove(pageId);
                        plugin.getDataStorage().saveGUIsAsync();
                        GUIEditor.openEditor(player, gui, Math.max(1, pageId - 1));
                    }
                } else if (slot == 4 && pageId > 1) {
                    GUIEditor.openEditor(player, gui, pageId - 1);
                } else if (slot == 5 && pageId < gui.pages().size()) {
                    GUIEditor.openEditor(player, gui, pageId + 1);
                } else if (slot == 6) {
                    gui.pages().put(pageId, new GUIPage(page.items(), !page.allowInteract()));
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, gui, pageId);
                } else if (slot >= 9 && slot < 9 + gui.rows() * 9) {
                    GUIEditor.openItemSelect(player, gui, slot - 9, pageId);
                }
            } else if (title.equals("選擇行數")) {
                event.setCancelled(true);
                if (slot >= 0 && slot < 6) {
                    int newRows = slot + 1;
                    CustomGUI updatedGui = new CustomGUI(gui.name(), newRows, gui.pages());
                    plugin.getGuiManager().getGUIs().put(gui.name(), updatedGui);
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, updatedGui, pageId);
                }
            } else if (title.equals("選擇物品")) {
                event.setCancelled(true);
                if (slot >= 0 && slot < GUIEditor.COMMON_ITEMS.length) {
                    page.items().put(editorHolder.getSlot(), new GUIItem(GUIEditor.COMMON_ITEMS[slot].name(), null, null, true, new ArrayList<>()));
                    gui.pages().put(pageId, page);
                    plugin.getGuiManager().getGUIs().put(gui.name(), gui);
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openActionSelect(player, gui, editorHolder.getSlot(), pageId);
                } else if (slot == 10) {
                    plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_item_id", editorHolder.getSlot(), pageId));
                    player.closeInventory();
                    player.sendMessage("§a請輸入物品 ID（例如 minecraft:stone，輸入 /coderyogui cancel 取消）");
                }
            } else if (title.equals("選擇動作")) {
                event.setCancelled(true);
                if (slot >= 0 && slot <= 7) {
                    GUIItem item = page.items().get(editorHolder.getSlot());
                    List<GUIAction> actions = new ArrayList<>(item.actions());
                    switch (slot) {
                        case 0 -> {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_command", editorHolder.getSlot(), pageId, new ArrayList<>()));
                            player.closeInventory();
                            player.sendMessage("§a請輸入命令（例如 say Hello，不包含 /，輸入 /coderyogui cancel 取消）");
                        }
                        case 1 -> {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_command", editorHolder.getSlot(), pageId, new ArrayList<>(Collections.singletonList("console"))));
                            player.closeInventory();
                            player.sendMessage("§a請輸入控制台命令（例如 say Hello from console，不包含 /，輸入 /coderyogui cancel 取消）");
                        }
                        case 2 -> {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_message", editorHolder.getSlot(), pageId));
                            player.closeInventory();
                            player.sendMessage("§a請輸入訊息（輸入 /coderyogui cancel 取消）");
                        }
                        case 3 -> {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_page", editorHolder.getSlot(), pageId));
                            player.closeInventory();
                            player.sendMessage("§a請輸入目標頁面 ID（輸入 /coderyogui cancel 取消）");
                        }
                        case 4 -> {
                            GUIEditor.openSoundSelect(player, gui, editorHolder.getSlot(), pageId);
                        }
                        case 5 -> {
                            page.items().put(editorHolder.getSlot(), new GUIItem(item.material(), item.name(), item.lore(), !item.takeable(), actions));
                            gui.pages().put(pageId, page);
                            plugin.getGuiManager().getGUIs().put(gui.name(), gui);
                            plugin.getDataStorage().saveGUIsAsync();
                            GUIEditor.openEditor(player, gui, pageId);
                        }
                        case 6 -> {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_item_name", editorHolder.getSlot(), pageId));
                            player.closeInventory();
                            player.sendMessage("§a請輸入物品名稱（輸入 /coderyogui cancel 取消）");
                        }
                        case 7 -> {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_lore_line", editorHolder.getSlot(), pageId));
                            player.closeInventory();
                            player.sendMessage("§a請輸入第一行 Lore（輸入 /coderyogui cancel 結束）");
                        }
                    }
                }
            } else if (title.equals("選擇音效")) {
                event.setCancelled(true);
                if (slot >= 0 && slot < GUIEditor.COMMON_SOUNDS.length) {
                    GUIItem item = page.items().get(editorHolder.getSlot());
                    List<GUIAction> actions = new ArrayList<>(item.actions());
                    actions.add(new GUIAction("sound", GUIEditor.COMMON_SOUNDS[slot].name(), false));
                    page.items().put(editorHolder.getSlot(), new GUIItem(item.material(), item.name(), item.lore(), true, actions));
                    gui.pages().put(pageId, page);
                    plugin.getGuiManager().getGUIs().put(gui.name(), gui);
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, gui, pageId);
                } else if (slot == 10) {
                    plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_sound", editorHolder.getSlot(), pageId));
                    player.closeInventory();
                    player.sendMessage("§a請輸入音效 ID（例如 ENTITY_EXPERIENCE_ORB_PICKUP，輸入 /coderyogui cancel 取消）");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        EditSession session = plugin.getEditSession(player.getUniqueId());
        if (session != null) {
            event.setCancelled(true);
            event.getRecipients().clear(); // 清空接收者，防止訊息傳播
            String input = event.getMessage();
            plugin.getLogger().info("Processing chat input: " + input + " for player: " + player.getName() + ", cancelled: " + event.isCancelled());
            new BukkitRunnable() {
                @Override
                public void run() {
                    String newGuiName = session.handleInput(player, input, plugin);
                    plugin.getLogger().info("Chat input result: newGuiName = " + newGuiName);
                    if (newGuiName != null) {
                        plugin.getDataStorage().saveGUIsAsync();
                        CustomGUI updatedGui = plugin.getGuiManager().getGUI(newGuiName);
                        if (updatedGui != null) {
                            plugin.getLogger().info("Opening editor for GUI: " + newGuiName);
                            GUIEditor.openEditor(player, updatedGui, session.pageId());
                        } else {
                            player.sendMessage("§c無法找到更新的 GUI，請重新編輯！");
                            plugin.getLogger().warning("Failed to find GUI: " + newGuiName);
                        }
                        plugin.setEditSession(player.getUniqueId(), null);
                    } else {
                        player.sendMessage("§c無效輸入，請重新輸入或使用 /coderyogui cancel");
                    }
                }
            }.runTask(plugin);
        }
    }
}