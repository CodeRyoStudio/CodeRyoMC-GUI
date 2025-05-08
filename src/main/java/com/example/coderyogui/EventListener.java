package com.example.coderyogui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

public class EventListener implements Listener {
    private final CoderyoGUI plugin;

    public EventListener(CoderyoGUI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GUIHolder guiHolder) {
            event.setCancelled(true);
            CustomGUI gui = guiHolder.getGUI();
            GUIPage page = gui.pages().get(guiHolder.getPageId());
            GUIItem item = page.items().get(event.getSlot());
            if (item != null && (!item.takeable() || !page.allowTake())) {
                item.actions().forEach(action -> action.execute(player));
            }
        } else if (holder instanceof EditorHolder editorHolder) {
            event.setCancelled(true);
            CustomGUI gui = editorHolder.getGUI();
            int pageId = editorHolder.getPageId();
            int slot = event.getSlot();
            if (slot == 0) {
                plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_name", -1, pageId));
                player.closeInventory();
                player.sendMessage("§a請輸入 GUI 名稱（輸入 /coderyogui cancel 取消）");
            } else if (slot == 1) {
                int newRows = gui.rows() % 6 + 1;
                plugin.getGuiManager().getGUIs().put(gui.name(), new CustomGUI(gui.name(), newRows, gui.pages()));
                plugin.getDataStorage().saveGUIsAsync();
                GUIEditor.openEditor(player, gui, pageId);
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
            } else if (slot == 4) {
                int newPageId = (pageId % gui.pages().size()) + 1;
                GUIEditor.openEditor(player, gui, newPageId);
            } else if (slot == 5) {
                GUIPage page = gui.pages().get(pageId);
                gui.pages().put(pageId, new GUIPage(page.items(), !page.allowTake(), page.allowPlace()));
                plugin.getDataStorage().saveGUIsAsync();
                GUIEditor.openEditor(player, gui, pageId);
            } else if (slot == 6) {
                GUIPage page = gui.pages().get(pageId);
                gui.pages().put(pageId, new GUIPage(page.items(), page.allowTake(), !page.allowPlace()));
                plugin.getDataStorage().saveGUIsAsync();
                GUIEditor.openEditor(player, gui, pageId);
            } else if (slot >= 9 && slot < 9 + gui.rows() * 9) {
                GUIEditor.openItemSelect(player, gui, slot - 9, pageId);
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
                    if (session.handleInput(player, input, plugin)) {
                        plugin.getDataStorage().saveGUIsAsync();
                        GUIEditor.openEditor(player, plugin.getGuiManager().getGUI(session.gui().name()), session.pageId());
                        plugin.setEditSession(player.getUniqueId(), null);
                    } else {
                        player.sendMessage("§c無效輸入，請重新輸入或使用 /coderyogui cancel");
                    }
                }
            }.runTask(plugin);
        }
    }
}