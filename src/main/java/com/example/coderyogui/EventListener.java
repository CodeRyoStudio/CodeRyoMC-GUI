package com.example.coderyogui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventListener implements Listener {
    private final CoderyoGUI plugin;
    private final InputHandler inputHandler;

    public EventListener(CoderyoGUI plugin) {
        this.plugin = plugin;
        this.inputHandler = new InputHandler(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryHolder holder = event.getInventory().getHolder();
        String title = event.getView().getTitle();
        int slot = event.getSlot();
        int rawSlot = event.getRawSlot();

        // 僅處理頂部庫存的點擊
        if (rawSlot != slot || rawSlot >= event.getInventory().getSize()) {
            return;
        }

        if (holder instanceof MainMenuHolder menuHolder) {
            event.setCancelled(true);
            plugin.getLogger().info("玩家 " + player.getName() + " 點擊主菜單，槽位 " + slot);
            if (slot == 0) {
                if (!player.hasPermission("coderyogui.create")) {
                    player.sendMessage("§c無權限創建 GUI！");
                    return;
                }
                plugin.setEditSession(player.getUniqueId(), new EditSession(null, "create_gui", -1, 1));
                player.closeInventory();
                inputHandler.openSignInput(player, "輸入新 GUI 名稱", input -> handleInput(player, input));
            } else if (slot == 1) {
                if (!player.hasPermission("coderyogui.use")) {
                    player.sendMessage("§c無權限打開 GUI！"); 
                    return;
                }
                new GUIListGUI(plugin, 1, false, null).open(player);
            } else if (slot == 2) {
                if (!player.hasPermission("coderyogui.edit.*")) {
                    player.sendMessage("§c無權限編輯 GUI！");
                    return;
                }
                new GUIListGUI(plugin, 1, true, null).open(player);
            } else if (slot == 3) {
                if (!player.hasPermission("coderyogui.del")) {
                    player.sendMessage("§c無權限刪除 GUI！");
                    return;
                }
                new DeleteGUIListGUI(plugin, 1, null).open(player);
            } else if (slot >= 9 && slot < 54) {
                List<String> guiNames = new ArrayList<>(plugin.getGuiManager().getGUIs().keySet());
                int index = (menuHolder.getPage() - 1) * 45 + (slot - 9);
                if (index < guiNames.size()) {
                    String guiName = guiNames.get(index);
                    if (event.getClick() == ClickType.LEFT) {
                        if (!player.hasPermission("coderyogui.use")) {
                            player.sendMessage("§c無權限打開 GUI！");
                            return;
                        }
                        CustomGUI gui = plugin.getGuiManager().getGUI(guiName);
                        if (gui != null) {
                            player.openInventory(gui.getPage(1));
                            player.sendMessage("§a已打開 GUI: " + guiName);
                        } else {
                            player.sendMessage("§cGUI 不存在！");
                        }
                    } else if (event.getClick() == ClickType.RIGHT) {
                        if (!player.hasPermission("coderyogui.edit." + guiName)) {
                            player.sendMessage("§c無權限編輯 GUI！");
                            return;
                        }
                        CustomGUI gui = plugin.getGuiManager().getGUI(guiName);
                        if (gui != null) {
                            // 確保頁面存在
                            if (!gui.pages().containsKey(1)) {
                                gui.pages().put(1, new GUIPage());
                                plugin.getLogger().warning("GUI " + guiName + " 缺少 pageId=1，已自動創建");
                                plugin.getDataStorage().saveGUIsAsync();
                            }
                            GUIEditor.openEditor(player, gui, 1);
                            player.sendMessage("§a正在編輯 GUI: " + guiName);
                        } else {
                            player.sendMessage("§cGUI 不存在！");
                        }
                    }
                }
            } else if (slot == 52 && menuHolder.getPage() > 1) {
                new MainMenuGUI(plugin, menuHolder.getPage() - 1).open(player);
                player.sendMessage("§a已切換到上一頁");
            } else if (slot == 53) {
                new MainMenuGUI(plugin, menuHolder.getPage() + 1).open(player);
                player.sendMessage("§a已切換到下一頁");
            }
        } else if (holder instanceof GUIListHolder listHolder) {
            event.setCancelled(true);
            plugin.getLogger().info("玩家 " + player.getName() + " 點擊 GUI 選擇列表，槽位 " + slot + "，模式: " + (listHolder.isEditMode() ? "編輯" : "打開"));
            if (slot == 0) {
                new MainMenuGUI(plugin, 1).open(player);
                player.sendMessage("§a已返回主菜單");
            } else if (slot == 1) {
                plugin.setEditSession(player.getUniqueId(), new EditSession(null, "search_gui", -1, listHolder.getPage(), new ArrayList<>(Collections.singletonList(listHolder.isEditMode() ? "edit" : "open"))));
                player.closeInventory();
                inputHandler.openSignInput(player, "輸入 GUI 名稱關鍵詞", input -> handleInput(player, input));
            } else if (slot >= 9 && slot < 54) {
                List<String> guiNames = new ArrayList<>(plugin.getGuiManager().getGUIs().keySet()).stream()
                        .filter(name -> listHolder.getSearch() == null || name.toLowerCase().contains(listHolder.getSearch().toLowerCase()))
                        .collect(Collectors.toList());
                int index = (listHolder.getPage() - 1) * 45 + (slot - 9);
                if (index < guiNames.size()) {
                    String guiName = guiNames.get(index);
                    CustomGUI gui = plugin.getGuiManager().getGUI(guiName);
                    if (gui == null) {
                        player.sendMessage("§cGUI 不存在！");
                        return;
                    }
                    if (listHolder.isEditMode()) {
                        if (!player.hasPermission("coderyogui.edit." + guiName)) {
                            player.sendMessage("§c無權限編輯 GUI！");
                            return;
                        }
                        // 確保頁面存在
                        if (!gui.pages().containsKey(1)) {
                            gui.pages().put(1, new GUIPage());
                            plugin.getLogger().warning("GUI " + guiName + " 缺少 pageId=1，已自動創建");
                            plugin.getDataStorage().saveGUIsAsync();
                        }
                        GUIEditor.openEditor(player, gui, 1);
                        player.sendMessage("§a正在編輯 GUI: " + guiName);
                    } else {
                        if (!player.hasPermission("coderyogui.use")) {
                            player.sendMessage("§c無權限打開 GUI！");
                            return;
                        }
                        player.openInventory(gui.getPage(1));
                        player.sendMessage("§a已打開 GUI: " + guiName);
                    }
                }
            } else if (slot == 52 && listHolder.getPage() > 1) {
                new GUIListGUI(plugin, listHolder.getPage() - 1, listHolder.isEditMode(), listHolder.getSearch()).open(player);
                player.sendMessage("§a已切換到上一頁");
            } else if (slot == 53) {
                new GUIListGUI(plugin, listHolder.getPage() + 1, listHolder.isEditMode(), listHolder.getSearch()).open(player);
                player.sendMessage("§a已切換到下一頁");
            }
        } else if (holder instanceof DeleteGUIListHolder deleteHolder) {
            event.setCancelled(true);
            plugin.getLogger().info("玩家 " + player.getName() + " 點擊刪除 GUI 列表，槽位 " + slot);
            if (slot == 0) {
                new MainMenuGUI(plugin, 1).open(player);
                player.sendMessage("§a已返回主菜單");
            } else if (slot == 1) {
                plugin.setEditSession(player.getUniqueId(), new EditSession(null, "search_gui", -1, deleteHolder.getPage(), new ArrayList<>(Collections.singletonList("delete"))));
                player.closeInventory();
                inputHandler.openSignInput(player, "輸入 GUI 名稱關鍵詞", input -> handleInput(player, input));
            } else if (slot >= 9 && slot < 54) {
                List<String> guiNames = new ArrayList<>(plugin.getGuiManager().getGUIs().keySet()).stream()
                        .filter(name -> deleteHolder.getSearch() == null || name.toLowerCase().contains(deleteHolder.getSearch().toLowerCase()))
                        .collect(Collectors.toList());
                int index = (deleteHolder.getPage() - 1) * 45 + (slot - 9);
                if (index < guiNames.size()) {
                    String guiName = guiNames.get(index);
                    if (!player.hasPermission("coderyogui.del")) {
                        player.sendMessage("§c無權限刪除 GUI！");
                        return;
                    }
                    DeleteGUIListGUI.openConfirm(player, guiName);
                }
            } else if (slot == 52 && deleteHolder.getPage() > 1) {
                new DeleteGUIListGUI(plugin, deleteHolder.getPage() - 1, deleteHolder.getSearch()).open(player);
                player.sendMessage("§a已切換到上一頁");
            } else if (slot == 53) {
                new DeleteGUIListGUI(plugin, deleteHolder.getPage() + 1, deleteHolder.getSearch()).open(player);
                player.sendMessage("§a已切換到下一頁");
            }
        } else if (holder instanceof DeleteConfirmHolder confirmHolder) {
            event.setCancelled(true);
            plugin.getLogger().info("玩家 " + player.getName() + " 點擊刪除確認界面，槽位 " + slot);
            if (slot == 3) {
                String guiName = confirmHolder.getGuiName();
                plugin.getGuiManager().getGUIs().remove(guiName);
                plugin.getDataStorage().saveGUIsAsync();
                player.sendMessage("§a已刪除 GUI: " + guiName);
                new MainMenuGUI(plugin, 1).open(player);
            } else if (slot == 5) {
                new MainMenuGUI(plugin, 1).open(player);
                player.sendMessage("§a已取消刪除");
            }
        } else if (holder instanceof EditorHolder editorHolder) {
            CustomGUI gui = editorHolder.getGUI();
            int pageId = editorHolder.getPageId();
            // 確保頁面存在
            if (!gui.pages().containsKey(pageId)) {
                gui.pages().put(pageId, new GUIPage());
                plugin.getLogger().warning("GUI " + gui.name() + " 缺少 pageId=" + pageId + "，已自動創建");
                plugin.getDataStorage().saveGUIsAsync();
            }
            GUIPage page = gui.pages().get(pageId);

            if (title.startsWith("編輯: ")) {
                event.setCancelled(true);
                if (gui == null || page == null) {
                    player.sendMessage("§c無效 GUI 或頁面！");
                    new MainMenuGUI(plugin, 1).open(player);
                    return;
                }
                if (slot == 0) {
                    new MainMenuGUI(plugin, 1).open(player);
                    player.sendMessage("§a已返回主菜單");
                } else if (slot == 1) {
                    plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_name", -1, pageId));
                    player.closeInventory();
                    inputHandler.openSignInput(player, "輸入 GUI 名稱", input -> handleInput(player, input));
                } else if (slot == 2) {
                    GUIEditor.openRowSelect(player, gui, pageId);
                } else if (slot == 3) {
                    int newPageId = gui.pages().size() + 1;
                    gui.pages().put(newPageId, new GUIPage());
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, gui, newPageId);
                } else if (slot == 4) {
                    if (gui.pages().size() > 1) {
                        gui.pages().remove(pageId);
                        plugin.getDataStorage().saveGUIsAsync();
                        GUIEditor.openEditor(player, gui, Math.max(1, pageId - 1));
                    }
                } else if (slot == 5 && pageId > 1) {
                    GUIEditor.openEditor(player, gui, pageId - 1);
                } else if (slot == 6 && pageId < gui.pages().size()) {
                    GUIEditor.openEditor(player, gui, pageId + 1);
                } else if (slot == 7) {
                    gui.pages().put(pageId, new GUIPage(page.items(), !page.allowInteract()));
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, gui, pageId);
                } else if (slot == 8) {
                    player.openInventory(gui.getPage(pageId));
                } else if (slot >= 9 && slot < 9 + gui.rows() * 9) {
                    if (event.getClick() == ClickType.RIGHT) {
                        // 將槽位從編輯介面的 0-based 調整為最終 GUI 的 1-based
                        int adjustedSlot = slot - 9;
                        GUIEditor.openContextMenu(player, gui, adjustedSlot, pageId);
                    }
                }
            } else if (title.equals("物品設置")) {
                event.setCancelled(true);
                if (gui == null) {
                    player.sendMessage("§c無效 GUI！");
                    new MainMenuGUI(plugin, 1).open(player);
                    return;
                }
                int itemSlot = editorHolder.getSlot();
                switch (slot) {
                    case 0:
                        GUIEditor.openItemSelect(player, gui, itemSlot, pageId, null, 1);
                        break;
                    case 1:
                        plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_command", itemSlot, pageId));
                        player.closeInventory();
                        inputHandler.openSignInput(player, "輸入玩家命令（不含 /）", input -> handleInput(player, input));
                        break;
                    case 2:
                        plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_command", itemSlot, pageId, new ArrayList<>(Collections.singletonList("console"))));
                        player.closeInventory();
                        inputHandler.openSignInput(player, "輸入控制台命令（不含 /）", input -> handleInput(player, input));
                        break;
                    case 3:
                        plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_item_name", itemSlot, pageId));
                        player.closeInventory();
                        inputHandler.openSignInput(player, "輸入物品名稱", input -> handleInput(player, input));
                        break;
                    case 4:
                        GUIEditor.openEditor(player, gui, pageId);
                        break;
                }
            } else if (title.startsWith("選擇物品") || title.startsWith("搜尋物品")) {
                event.setCancelled(true);
                if (gui == null) {
                    player.sendMessage("§c無效 GUI！");
                    new MainMenuGUI(plugin, 1).open(player);
                    return;
                }
                String search = editorHolder.getSearch();
                int searchPage = editorHolder.getSearchPage();
                plugin.getLogger().info("玩家 " + player.getName() + " 點擊物品選擇列表，槽位 " + slot + "，搜尋: " + (search != null ? search : "無") + "，頁數: " + searchPage);
                if (slot == 0) {
                    GUIEditor.openContextMenu(player, gui, editorHolder.getSlot(), pageId);
                } else if (slot == 1) {
                    plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "search_item", editorHolder.getSlot(), pageId));
                    player.closeInventory();
                    inputHandler.openSignInput(player, "輸入物品名稱（如 diamond）", input -> handleInput(player, input));
                } else if (slot == 52) {
                    if (searchPage > 1) {
                        GUIEditor.openItemSelect(player, gui, editorHolder.getSlot(), pageId, search, searchPage - 1);
                        player.sendMessage("§a已切換到上一頁");
                    } else {
                        player.sendMessage("§7已是第一頁");
                    }
                } else if (slot == 53) {
                    GUIEditor.openItemSelect(player, gui, editorHolder.getSlot(), pageId, search, searchPage + 1);
                    player.sendMessage("§a已切換到下一頁");
                } else if (slot >= 9 && slot < 52) { // 排除分頁按鈕
                    List<Material> materials = Arrays.stream(Material.values())
                            .filter(Material::isItem)
                            .filter(m -> m != Material.AIR)
                            .filter(m -> {
                                try {
                                    ItemStack item = new ItemStack(m);
                                    return item.getItemMeta() != null;
                                } catch (Exception e) {
                                    return false;
                                }
                            })
                            .filter(m -> search == null || m.name().toLowerCase().contains(search.toLowerCase()))
                            .collect(Collectors.toList());
                    int index = (searchPage - 1) * 45 + (slot - 9);
                    plugin.getLogger().info("物品選擇: 槽位 " + slot + "，索引 " + index + "，材質數量 " + materials.size());
                    if (index < materials.size()) {
                        Material material = materials.get(index);
                        page.items().put(editorHolder.getSlot(), new GUIItem(material.name(), null, null, true, new ArrayList<>()));
                        gui.pages().put(pageId, page);
                        plugin.getGuiManager().getGUIs().put(gui.name(), gui);
                        plugin.getDataStorage().saveGUIsAsync();
                        GUIEditor.openContextMenu(player, gui, editorHolder.getSlot(), pageId);
                    }
                }
            } else if (title.equals("選擇行數")) {
                event.setCancelled(true);
                if (slot == 0) {
                    GUIEditor.openEditor(player, gui, pageId);
                } else if (slot >= 0 && slot < 6) {
                    int newRows = slot + 1;
                    CustomGUI updatedGui = new CustomGUI(gui.name(), newRows, gui.pages());
                    plugin.getGuiManager().getGUIs().put(gui.name(), updatedGui);
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, updatedGui, pageId);
                }
            }
        } else if (holder instanceof GUIHolder guiHolder) {
            CustomGUI gui = guiHolder.getGUI();
            int pageId = guiHolder.getPageId();
            // 確保頁面存在
            if (!gui.pages().containsKey(pageId)) {
                gui.pages().put(pageId, new GUIPage());
                plugin.getLogger().warning("GUI " + gui.name() + " 缺少 pageId=" + pageId + "，已自動創建");
                plugin.getDataStorage().saveGUIsAsync();
            }
            GUIPage page = gui.pages().get(pageId);
            GUIItem item = page.items().get(slot);
            if (slot == 0) {
                event.setCancelled(true);
                new MainMenuGUI(plugin, 1).open(player);
                player.sendMessage("§a已返回主菜單");
            } else if (page.allowInteract()) {
                event.setCancelled(false);
                if (item != null && !item.actions().isEmpty()) {
                    item.actions().forEach(action -> action.execute(player));
                }
            } else {
                event.setCancelled(true);
                if (item != null && !item.actions().isEmpty()) {
                    item.actions().forEach(action -> action.execute(player));
                }
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        EditSession session = plugin.getEditSession(player.getUniqueId());
        if (session != null) {
            // 合併多行輸入
            String input = Arrays.stream(event.getLines())
                    .filter(line -> line != null && !line.trim().isEmpty())
                    .collect(Collectors.joining(" ")).trim();
            plugin.getLogger().info("玩家 " + player.getName() + " 輸入告示牌內容: " + input + "，狀態: " + session.state());
            new BukkitRunnable() {
                @Override
                public void run() {
                    String newGuiName = session.handleInput(player, input, plugin);
                    if (newGuiName != null) {
                        plugin.getDataStorage().saveGUIsAsync();
                        CustomGUI updatedGui = plugin.getGuiManager().getGUI(newGuiName);
                        if (updatedGui != null) {
                            // 確保頁面存在
                            if (!updatedGui.pages().containsKey(session.pageId())) {
                                updatedGui.pages().put(session.pageId(), new GUIPage());
                                plugin.getLogger().warning("GUI " + newGuiName + " 缺少 pageId=" + session.pageId() + "，已自動創建");
                                plugin.getDataStorage().saveGUIsAsync();
                            }
                            GUIEditor.openEditor(player, updatedGui, session.pageId());
                        } else if (session.state().equals("create_gui")) {
                            CustomGUI newGui = plugin.getGuiManager().getGUI(newGuiName);
                            // 確保新創建的 GUI 有第一頁
                            if (!newGui.pages().containsKey(1)) {
                                newGui.pages().put(1, new GUIPage());
                                plugin.getLogger().warning("新創建的 GUI " + newGuiName + " 缺少 pageId=1，已自動創建");
                                plugin.getDataStorage().saveGUIsAsync();
                            }
                            GUIEditor.openEditor(player, newGui, 1);
                        } else if (session.state().equals("search_item")) {
                            GUIEditor.openItemSelect(player, session.gui(), session.slot(), session.pageId(), input, 1);
                        } else if (session.state().equals("search_gui")) {
                            boolean isEditMode = session.tempData().contains("edit");
                            boolean isDeleteMode = session.tempData().contains("delete");
                            if (isDeleteMode) {
                                new DeleteGUIListGUI(plugin, session.pageId(), input).open(player);
                            } else {
                                new GUIListGUI(plugin, session.pageId(), isEditMode, input).open(player);
                            }
                        } else {
                            player.sendMessage("§c無法找到更新的 GUI，請重新編輯！");
                        }
                        plugin.setEditSession(player.getUniqueId(), null);
                    } else {
                        player.sendMessage("§c無效輸入，請重新輸入");
                        inputHandler.openSignInput(player, "重新輸入", in -> handleInput(player, in));
                    }
                }
            }.runTask(plugin);
        }
    }

    private void handleInput(Player player, String input) {
        EditSession session = plugin.getEditSession(player.getUniqueId());
        if (session == null) {
            player.sendMessage("§c輸入會話已過期，請重新操作！");
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                String newGuiName = session.handleInput(player, input, plugin);
                if (newGuiName != null) {
                    plugin.getDataStorage().saveGUIsAsync();
                    CustomGUI updatedGui = plugin.getGuiManager().getGUI(newGuiName);
                    if (updatedGui != null) {
                        // 確保頁面存在
                        if (!updatedGui.pages().containsKey(session.pageId())) {
                            updatedGui.pages().put(session.pageId(), new GUIPage());
                            plugin.getLogger().warning("GUI " + newGuiName + " 缺少 pageId=" + session.pageId() + "，已自動創建");
                            plugin.getDataStorage().saveGUIsAsync();
                        }
                        GUIEditor.openEditor(player, updatedGui, session.pageId());
                    } else if (session.state().equals("create_gui")) {
                        CustomGUI newGui = plugin.getGuiManager().getGUI(newGuiName);
                        // 確保新創建的 GUI 有第一頁
                        if (!newGui.pages().containsKey(1)) {
                            newGui.pages().put(1, new GUIPage());
                            plugin.getLogger().warning("新創建的 GUI " + newGuiName + " 缺少 pageId=1，已自動創建");
                            plugin.getDataStorage().saveGUIsAsync();
                        }
                        GUIEditor.openEditor(player, newGui, 1);
                    } else if (session.state().equals("search_item")) {
                        GUIEditor.openItemSelect(player, session.gui(), session.slot(), session.pageId(), input, 1);
                    } else if (session.state().equals("search_gui")) {
                        boolean isEditMode = session.tempData().contains("edit");
                        boolean isDeleteMode = session.tempData().contains("delete");
                        if (isDeleteMode) {
                            new DeleteGUIListGUI(plugin, session.pageId(), input).open(player);
                        } else {
                            new GUIListGUI(plugin, session.pageId(), isEditMode, input).open(player);
                        }
                    } else {
                        player.sendMessage("§c無法找到更新的 GUI，請重新編輯！");
                    }
                    plugin.setEditSession(player.getUniqueId(), null);
                } else {
                    player.sendMessage("§c無效輸入，請重新輸入");
                    inputHandler.openSignInput(player, "重新輸入", in -> handleInput(player, in));
                }
            }
        }.runTask(plugin);
    }
}