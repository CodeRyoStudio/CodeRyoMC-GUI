package com.example.coderyogui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
            event.setCancelled(true);
            CustomGUI gui = guiHolder.getGUI();
            GUIPage page = gui.pages().get(guiHolder.getPageId());
            GUIItem item = page.items().get(slot);
            if (item != null && (!item.takeable() || !page.allowTake())) {
                item.actions().forEach(action -> action.execute(player));
            }
        } else if (holder instanceof EditorHolder editorHolder) {
            event.setCancelled(true);
            CustomGUI gui = editorHolder.getGUI();
            int pageId = editorHolder.getPageId();

            if (title.startsWith("編輯: ")) {
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
                    GUIEditor.openEditor(player, gui, pageId - 1); // 上一頁
                } else if (slot == 5 && pageId < gui.pages().size()) {
                    GUIEditor.openEditor(player, gui, pageId + 1); // 下一頁
                } else if (slot == 6) {
                    GUIPage page = gui.pages().get(pageId);
                    gui.pages().put(pageId, new GUIPage(page.items(), !page.allowTake(), page.allowPlace()));
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, gui, pageId);
                } else if (slot == 7) {
                    GUIPage page = gui.pages().get(pageId);
                    gui.pages().put(pageId, new GUIPage(page.items(), page.allowTake(), !page.allowPlace()));
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, gui, pageId);
                } else if (slot >= 9 && slot < 9 + gui.rows() * 9) {
                    GUIEditor.openItemSelect(player, gui, slot - 9, pageId);
                }
            } else if (title.equals("選擇行數")) {
                if (slot >= 0 && slot < 6) {
                    int newRows = slot + 1;
                    CustomGUI updatedGui = new CustomGUI(gui.name(), newRows, gui.pages());
                    plugin.getGuiManager().getGUIs().put(gui.name(), updatedGui);
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, updatedGui, pageId);
                }
            } else if (title.equals("選擇物品")) {
                if (slot >= 0 && slot < GUIEditor.COMMON_ITEMS.length) {
                    GUIPage page = gui.pages().get(pageId);
                    page.items().put(slot, new GUIItem(GUIEditor.COMMON_ITEMS[slot].name(), null, null, false, new ArrayList<>()));
                    gui.pages().put(pageId, page);
                    plugin.getGuiManager().getGUIs().put(gui.name(), gui);
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, gui, pageId);
                } else if (slot == 10) {
                    plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_item_id", slot, pageId));
                    player.closeInventory();
                    player.sendMessage("§a請輸入物品 ID（例如 minecraft:stone，輸入 /coderyogui cancel 取消）");
                }
            } else if (title.equals("選擇動作")) {
                if (slot >= 0 && slot <= 7) {
                    GUIPage page = gui.pages().get(pageId);
                    GUIItem item = page.items().get(slot);
                    List<GUIAction> actions = new ArrayList<>(item.actions());
                    switch (slot) {
                        case 0 -> {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_command", slot, pageId));
                            player.closeInventory();
                            player.sendMessage("§a請輸入命令（輸入 /coderyogui cancel 取消）");
                        }
                        case 1 -> {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_command", slot, pageId, new ArrayList<>(Collections.singletonList("console"))));
                            player.closeInventory();
                            player.sendMessage("§a請輸入控制台命令（輸入 /coderyogui cancel 取消）");
                        }
                        case 2 -> {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_message", slot, pageId));
                            player.closeInventory();
                            player.sendMessage("§a請輸入訊息（輸入 /coderyogui cancel 取消）");
                        }
                        case 3 -> {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_page", slot, pageId));
                            player.closeInventory();
                            player.sendMessage("§a請輸入目標頁面 ID（輸入 /coderyogui cancel 取消）");
                        }
                        case 4 -> {
                            GUIEditor.openSoundSelect(player, gui, slot, pageId);
                        }
                        case 5 -> {
                            page.items().put(slot, new GUIItem(item.material(), item.name(), item.lore(), !item.takeable(), actions));
                            gui.pages().put(pageId, page);
                            plugin.getGuiManager().getGUIs().put(gui.name(), gui);
                            plugin.getDataStorage().saveGUIsAsync();
                            GUIEditor.openEditor(player, gui, pageId);
                        }
                        case 6 -> {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_item_name", slot, pageId));
                            player.closeInventory();
                            player.sendMessage("§a請輸入物品名稱（輸入 /coderyogui cancel 取消）");
                        }
                        case 7 -> {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_lore_line", slot, pageId));
                            player.closeInventory();
                            player.sendMessage("§a請輸入第一行 Lore（輸入 /coderyogui cancel 結束）");
                        }
                    }
                }
            } else if (title.equals("選擇音效")) {
                if (slot >= 0 && slot < GUIEditor.COMMON_SOUNDS.length) {
                    GUIPage page = gui.pages().get(pageId);
                    GUIItem item = page.items().get(slot);
                    List<GUIAction> actions = new ArrayList<>(item.actions());
                    actions.add(new GUIAction("sound", GUIEditor.COMMON_SOUNDS[slot].name(), false));
                    page.items().put(slot, new GUIItem(item.material(), item.name(), item.lore(), item.takeable(), actions));
                    gui.pages().put(pageId, page);
                    plugin.getGuiManager().getGUIs().put(gui.name(), gui);
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, gui, pageId);
                } else if (slot == 10) {
                    plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_sound", slot, pageId));
                    player.closeInventory();
                    player.sendMessage("§a請輸入音效 ID（例如 ENTITY_EXPERIENCE_ORB_PICKUP，輸入 /coderyogui cancel 取消）");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        EditSession session = plugin.getEditSession(player.getUniqueId());
        if (session != null) {
            event.setCancelled(true);
            String input = event.getMessage();
            new BukkitRunnable() {
                @Override
                public void run() {
                    String newGuiName = session.handleInput(player, input, plugin);
                    if (newGuiName != null) {
                        plugin.getDataStorage().saveGUIsAsync();
                        CustomGUI updatedGui = plugin.getGuiManager().getGUI(newGuiName);
                        if (updatedGui != null) {
                            GUIEditor.openEditor(player, updatedGui, session.pageId());
                        } else {
                            player.sendMessage("§c無法找到更新的 GUI，請重新編輯！");
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