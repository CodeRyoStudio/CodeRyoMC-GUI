package com.coderyo.coderyogui;

import com.coderyo.coderyogui.api.GUIClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

import static com.coderyo.coderyogui.Constants.*;

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

        if (rawSlot != slot || rawSlot >= event.getInventory().getSize()) {
            return;
        }

        if (holder instanceof MainMenuHolder menuHolder) {
            event.setCancelled(true);
            plugin.getLogger().info(String.format(LOG_CLICKED_MAIN_MENU, player.getName(), slot));

            if (slot == MAIN_MENU_CREATE_BUTTON) {
                if (!player.hasPermission(PERMISSION_CREATE)) {
                    player.sendMessage(MSG_NO_PERMISSION_CREATE);
                    return;
                }
                plugin.setEditSession(player.getUniqueId(), new EditSession(null, STATE_CREATE_GUI, -1, DEFAULT_GUI_PAGE));
                player.closeInventory();
                inputHandler.openSignInput(player, PROMPT_ENTER_GUI_NAME, input -> handleInput(player, input));
            } else if (slot == MAIN_MENU_OPEN_BUTTON) {
                if (!player.hasPermission(PERMISSION_USE)) {
                    player.sendMessage(MSG_NO_PERMISSION_OPEN);
                    return;
                }
                new GUIListGUI(plugin, DEFAULT_GUI_PAGE, false, null).open(player);
            } else if (slot == MAIN_MENU_EDIT_BUTTON) {
                if (!player.hasPermission(PERMISSION_EDIT_ALL)) {
                    player.sendMessage(MSG_NO_PERMISSION_EDIT);
                    return;
                }
                new GUIListGUI(plugin, DEFAULT_GUI_PAGE, true, null).open(player);
            } else if (slot == MAIN_MENU_DELETE_BUTTON) {
                if (!player.hasPermission(PERMISSION_DELETE)) {
                    player.sendMessage(MSG_NO_PERMISSION_DELETE);
                    return;
                }
                new DeleteGUIListGUI(plugin, DEFAULT_GUI_PAGE, null).open(player);
            } else if (slot >= CONTENT_AREA_START && slot < INVENTORY_SIZE_54) {
                List<String> guiNames = new ArrayList<>(plugin.getGuiManager().getGUIs().keySet());
                int index = (menuHolder.getPage() - 1) * ITEMS_PER_PAGE + (slot - CONTENT_AREA_START);
                if (index < guiNames.size()) {
                    String guiName = guiNames.get(index);
                    if (event.getClick() == ClickType.LEFT) {
                        if (!player.hasPermission(PERMISSION_USE)) {
                            player.sendMessage(MSG_NO_PERMISSION_OPEN);
                            return;
                        }
                        CustomGUI gui = plugin.getGuiManager().getGUI(guiName);
                        if (gui != null) {
                            player.openInventory(gui.getPage(DEFAULT_GUI_PAGE));
                            player.sendMessage(MSG_GUI_OPENED + guiName);
                        } else {
                            player.sendMessage(MSG_GUI_NOT_FOUND);
                        }
                    } else if (event.getClick() == ClickType.RIGHT) {
                        if (!player.hasPermission(PERMISSION_EDIT_ALL)) {
                            player.sendMessage(MSG_NO_PERMISSION_EDIT);
                            return;
                        }
                        CustomGUI gui = plugin.getGuiManager().getGUI(guiName);
                        if (gui != null) {
                            if (!gui.pages().containsKey(DEFAULT_GUI_PAGE)) {
                                gui.pages().put(DEFAULT_GUI_PAGE, new GUIPage());
                                plugin.getLogger().warning(String.format(LOG_GUI_MISSING_PAGE, guiName, DEFAULT_GUI_PAGE));
                                plugin.getDataStorage().saveGUIsAsync();
                            }
                            GUIEditor.openEditor(player, gui, DEFAULT_GUI_PAGE);
                            player.sendMessage(MSG_GUI_EDITING + guiName);
                        } else {
                            player.sendMessage(MSG_GUI_DOES_NOT_EXIST);
                        }
                    }
                }
            } else if (slot == PREVIOUS_PAGE_BUTTON && menuHolder.getPage() > MIN_GUI_ROWS) {
                new MainMenuGUI(plugin, menuHolder.getPage() - 1).open(player);
                player.sendMessage(MSG_SWITCHED_TO_PREVIOUS_PAGE);
            } else if (slot == NEXT_PAGE_BUTTON) {
                new MainMenuGUI(plugin, menuHolder.getPage() + 1).open(player);
                player.sendMessage(MSG_SWITCHED_TO_NEXT_PAGE);
            }
        } else if (holder instanceof GUIListHolder listHolder) {
            event.setCancelled(true);
            plugin.getLogger().info("玩家 " + player.getName() + " 點擊 GUI 選擇列表，槽位 " + slot + "，模式: " + (listHolder.isEditMode() ? "編輯" : "打開"));
            if (slot == EDITOR_BACK_BUTTON) {
                new MainMenuGUI(plugin, DEFAULT_GUI_PAGE).open(player);
                player.sendMessage(MSG_RETURNED_TO_MAIN_MENU_ALT);
            } else if (slot == ITEM_SELECT_SEARCH_BUTTON) {
                plugin.setEditSession(player.getUniqueId(), new EditSession(null, STATE_SEARCH_GUI, DEFAULT_SLOT_VALUE, listHolder.getPage(), new ArrayList<>(Collections.singletonList(listHolder.isEditMode() ? TEMP_FLAG_EDIT : TEMP_FLAG_OPEN))));
                player.closeInventory();
                inputHandler.openSignInput(player, PROMPT_ENTER_GUI_NAME_KEYWORD, input -> handleInput(player, input));
            } else if (slot >= CONTENT_AREA_START && slot < INVENTORY_SIZE_54) {
                List<String> guiNames = new ArrayList<>(plugin.getGuiManager().getGUIs().keySet()).stream()
                        .filter(name -> listHolder.getSearch() == null || name.toLowerCase().contains(listHolder.getSearch().toLowerCase()))
                        .collect(Collectors.toList());
                int index = (listHolder.getPage() - 1) * ITEMS_PER_PAGE + (slot - CONTENT_AREA_START);
                if (index < guiNames.size()) {
                    String guiName = guiNames.get(index);
                    CustomGUI gui = plugin.getGuiManager().getGUI(guiName);
                    if (gui == null) {
                        player.sendMessage(MSG_GUI_DOES_NOT_EXIST);
                        return;
                    }
                    if (listHolder.isEditMode()) {
                        if (!player.hasPermission(PERMISSION_EDIT_ALL)) {
                            player.sendMessage(MSG_NO_PERMISSION_EDIT);
                            return;
                        }
                        if (!gui.pages().containsKey(DEFAULT_GUI_PAGE)) {
                            gui.pages().put(DEFAULT_GUI_PAGE, new GUIPage());
                            plugin.getLogger().warning(String.format(LOG_GUI_MISSING_PAGE, guiName, DEFAULT_GUI_PAGE));
                            plugin.getDataStorage().saveGUIsAsync();
                        }
                        GUIEditor.openEditor(player, gui, DEFAULT_GUI_PAGE);
                        player.sendMessage(MSG_GUI_EDITING + guiName);
                    } else {
                        if (!player.hasPermission(PERMISSION_USE)) {
                            player.sendMessage(MSG_NO_PERMISSION_OPEN);
                            return;
                        }
                        player.openInventory(gui.getPage(DEFAULT_GUI_PAGE));
                        player.sendMessage(MSG_GUI_OPENED + guiName);
                    }
                }
            } else if (slot == PREVIOUS_PAGE_BUTTON && listHolder.getPage() > MIN_GUI_ROWS) {
                new GUIListGUI(plugin, listHolder.getPage() - 1, listHolder.isEditMode(), listHolder.getSearch()).open(player);
                player.sendMessage(MSG_SWITCHED_TO_PREVIOUS_PAGE);
            } else if (slot == NEXT_PAGE_BUTTON) {
                new GUIListGUI(plugin, listHolder.getPage() + 1, listHolder.isEditMode(), listHolder.getSearch()).open(player);
                player.sendMessage(MSG_SWITCHED_TO_NEXT_PAGE);
            }
        } else if (holder instanceof DeleteGUIListHolder deleteHolder) {
            event.setCancelled(true);
            plugin.getLogger().info("玩家 " + player.getName() + " 點擊刪除 GUI 列表，槽位 " + slot);
            if (slot == EDITOR_BACK_BUTTON) {
                new MainMenuGUI(plugin, DEFAULT_GUI_PAGE).open(player);
                player.sendMessage(MSG_RETURNED_TO_MAIN_MENU_ALT);
            } else if (slot == ITEM_SELECT_SEARCH_BUTTON) {
                plugin.setEditSession(player.getUniqueId(), new EditSession(null, STATE_SEARCH_GUI, DEFAULT_SLOT_VALUE, deleteHolder.getPage(), new ArrayList<>(Collections.singletonList(TEMP_FLAG_DELETE))));
                player.closeInventory();
                inputHandler.openSignInput(player, PROMPT_ENTER_GUI_NAME_KEYWORD, input -> handleInput(player, input));
            } else if (slot >= CONTENT_AREA_START && slot < INVENTORY_SIZE_54) {
                List<String> guiNames = new ArrayList<>(plugin.getGuiManager().getGUIs().keySet()).stream()
                        .filter(name -> deleteHolder.getSearch() == null || name.toLowerCase().contains(deleteHolder.getSearch().toLowerCase()))
                        .collect(Collectors.toList());
                int index = (deleteHolder.getPage() - 1) * ITEMS_PER_PAGE + (slot - CONTENT_AREA_START);
                if (index < guiNames.size()) {
                    String guiName = guiNames.get(index);
                    if (!player.hasPermission(PERMISSION_DELETE)) {
                        player.sendMessage(MSG_NO_PERMISSION_DELETE);
                        return;
                    }
                    DeleteGUIListGUI.openConfirm(player, guiName);
                }
            } else if (slot == PREVIOUS_PAGE_BUTTON && deleteHolder.getPage() > MIN_GUI_ROWS) {
                new DeleteGUIListGUI(plugin, deleteHolder.getPage() - 1, deleteHolder.getSearch()).open(player);
                player.sendMessage(MSG_SWITCHED_TO_PREVIOUS_PAGE);
            } else if (slot == NEXT_PAGE_BUTTON) {
                new DeleteGUIListGUI(plugin, deleteHolder.getPage() + 1, deleteHolder.getSearch()).open(player);
                player.sendMessage(MSG_SWITCHED_TO_NEXT_PAGE);
            }
        } else if (holder instanceof DeleteConfirmHolder confirmHolder) {
            event.setCancelled(true);
            plugin.getLogger().info("玩家 " + player.getName() + " 點擊刪除確認界面，槽位 " + slot);
            if (slot == DELETE_CONFIRM_CONFIRM_BUTTON) {
                String guiName = confirmHolder.getGuiName();
                plugin.getGuiManager().getGUIs().remove(guiName);
                plugin.getDataStorage().saveGUIsAsync();
                player.sendMessage(MSG_GUI_DELETED + guiName);
                new MainMenuGUI(plugin, DEFAULT_GUI_PAGE).open(player);
            } else if (slot == DELETE_CONFIRM_CANCEL_BUTTON) {
                new MainMenuGUI(plugin, DEFAULT_GUI_PAGE).open(player);
                player.sendMessage(MSG_CANCELLED_DELETION);
            }
        } else if (holder instanceof EditorHolder editorHolder) {
            CustomGUI gui = editorHolder.getGUI();
            int pageId = editorHolder.getPageId();
            if (!gui.pages().containsKey(pageId)) {
                gui.pages().put(pageId, new GUIPage());
                plugin.getLogger().warning(String.format(LOG_GUI_MISSING_PAGE, gui.name(), pageId));
                plugin.getDataStorage().saveGUIsAsync();
            }
            GUIPage page = gui.pages().get(pageId);

            if (title.startsWith("編輯: ")) {
                event.setCancelled(true);
                if (gui == null || page == null) {
                                            player.sendMessage(MSG_INVALID_GUI_OR_PAGE);
                    new MainMenuGUI(plugin, DEFAULT_GUI_PAGE).open(player);
                    return;
                }
                if (slot == 0) {
                    new MainMenuGUI(plugin, DEFAULT_GUI_PAGE).open(player);
                    player.sendMessage(MSG_RETURNED_TO_MAIN_MENU_ALT);
                } else if (slot == 1) {
                    plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_name", -1, pageId));
                    player.closeInventory();
                    inputHandler.openSignInput(player, PROMPT_ENTER_GUI_NAME_ALT, input -> handleInput(player, input));
                } else if (slot == 2) {
                    GUIEditor.openRowSelect(player, gui, pageId);
                } else if (slot == EDITOR_ADD_PAGE_BUTTON) {
                    int newPageId = gui.pages().size() + 1;
                    gui.pages().put(newPageId, new GUIPage());
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, gui, newPageId);
                } else if (slot == EDITOR_DELETE_PAGE_BUTTON) {
                    if (gui.pages().size() > 1) {
                        gui.pages().remove(pageId);
                        plugin.getDataStorage().saveGUIsAsync();
                        GUIEditor.openEditor(player, gui, Math.max(1, pageId - 1));
                    }
                } else if (slot == EDITOR_PREVIOUS_PAGE_BUTTON && pageId > MIN_GUI_ROWS) {
                    GUIEditor.openEditor(player, gui, pageId - 1);
                } else if (slot == EDITOR_NEXT_PAGE_BUTTON && pageId < gui.pages().size()) {
                    GUIEditor.openEditor(player, gui, pageId + 1);
                } else if (slot == EDITOR_TOGGLE_INTERACT_BUTTON) {
                    gui.pages().put(pageId, new GUIPage(page.items(), !page.allowInteract()));
                    plugin.getDataStorage().saveGUIsAsync();
                    GUIEditor.openEditor(player, gui, pageId);
                } else if (slot == EDITOR_PREVIEW_BUTTON) {
                    player.openInventory(gui.getPage(pageId));
                } else if (slot >= CONTENT_AREA_START && slot < CONTENT_AREA_START + gui.rows() * 9) {
                    if (event.getClick() == ClickType.RIGHT) {
                        GUIEditor.openContextMenu(player, gui, slot, pageId);
                    }
                }
            } else if (title.equals("物品設置")) {
                event.setCancelled(true);
                if (gui == null) {
                    player.sendMessage(MSG_INVALID_GUI);
                    new MainMenuGUI(plugin, DEFAULT_GUI_PAGE).open(player);
                    return;
                }
                int itemSlot = editorHolder.getSlot();
                switch (slot) {
                    case CONTEXT_SELECT_ITEM_BUTTON:
                        GUIEditor.openItemSelect(player, gui, itemSlot, pageId, null, DEFAULT_GUI_PAGE);
                        break;
                    case CONTEXT_SET_PLAYER_COMMAND_BUTTON:
                        plugin.setEditSession(player.getUniqueId(), new EditSession(gui, STATE_SET_COMMAND, itemSlot, pageId));
                        player.closeInventory();
                        inputHandler.openSignInput(player, PROMPT_ENTER_PLAYER_COMMAND, input -> handleInput(player, input));
                        break;
                    case CONTEXT_SET_CONSOLE_COMMAND_BUTTON:
                        plugin.setEditSession(player.getUniqueId(), new EditSession(gui, STATE_SET_COMMAND, itemSlot, pageId, new ArrayList<>(Collections.singletonList(TEMP_FLAG_CONSOLE))));
                        player.closeInventory();
                        inputHandler.openSignInput(player, PROMPT_ENTER_CONSOLE_COMMAND, input -> handleInput(player, input));
                        break;
                    case CONTEXT_SET_NAME_BUTTON:
                        plugin.setEditSession(player.getUniqueId(), new EditSession(gui, STATE_SET_ITEM_NAME, itemSlot, pageId));
                        player.closeInventory();
                        inputHandler.openSignInput(player, PROMPT_ENTER_ITEM_NAME, input -> handleInput(player, input));
                        break;
                    case CONTEXT_DELETE_ITEM_BUTTON:
                        plugin.setEditSession(player.getUniqueId(), new EditSession(gui, STATE_DELETE_ITEM, itemSlot, pageId));
                        handleInput(player, EMPTY_STRING); // 直接處理刪除，無需輸入
                        GUIEditor.openEditor(player, gui, pageId);
                        break;
                    case CONTEXT_MANAGE_COMMANDS_BUTTON:
                        if (page.items().containsKey(itemSlot)) {
                            GUIEditor.openCommandManager(player, gui, itemSlot, pageId, DEFAULT_GUI_PAGE);
                        } else {
                            player.sendMessage(MSG_SET_ITEM_FIRST);
                        }
                        break;
                    case CONTEXT_BACK_BUTTON:
                        GUIEditor.openEditor(player, gui, pageId);
                        break;
                }
            } else if (title.startsWith("選擇物品") || title.startsWith("搜尋物品")) {
                event.setCancelled(true);
                if (gui == null) {
                                            player.sendMessage(MSG_INVALID_GUI);
                    new MainMenuGUI(plugin, DEFAULT_GUI_PAGE).open(player);
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
                } else if (slot >= 9 && slot < 54) {
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
            } else if (title.startsWith("管理命令: ")) {
                event.setCancelled(true);
                if (gui == null) {
                    player.sendMessage(MSG_INVALID_GUI);
                    new MainMenuGUI(plugin, DEFAULT_GUI_PAGE).open(player);
                    return;
                }
                int itemSlot = editorHolder.getSlot();
                int commandPage = editorHolder.getSearchPage();
                GUIItem item = page.items().get(itemSlot);
                List<GUIAction> actions = item != null ? item.actions() : new ArrayList<>();
                if (slot == 0) {
                    GUIEditor.openContextMenu(player, gui, itemSlot, pageId);
                } else if (slot == 1) {
                    List<String> tempData = new ArrayList<>();
                    tempData.add("new");
                    plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_command", itemSlot, pageId, tempData));
                    player.closeInventory();
                    inputHandler.openSignInput(player, "輸入新命令（不含 /）", input -> handleInput(player, input));
                } else if (slot == 52 && commandPage > 1) {
                    GUIEditor.openCommandManager(player, gui, itemSlot, pageId, commandPage - 1);
                    player.sendMessage("§a已切換到上一頁");
                } else if (slot == 53) {
                    GUIEditor.openCommandManager(player, gui, itemSlot, pageId, commandPage + 1);
                    player.sendMessage("§a已切換到下一頁");
                } else if (slot >= 9 && slot < 54) {
                    int index = (commandPage - 1) * 45 + (slot - 9);
                    if (index < actions.size()) {
                        if (event.getClick() == ClickType.LEFT) {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "edit_command", itemSlot, pageId, new ArrayList<>(Collections.singletonList(String.valueOf(index)))));
                            player.closeInventory();
                            inputHandler.openSignInput(player, "編輯命令（不含 /）", input -> handleInput(player, input));
                        } else if (event.getClick() == ClickType.RIGHT) {
                            plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "delete_command", itemSlot, pageId, new ArrayList<>(Collections.singletonList(String.valueOf(index)))));
                            handleInput(player, ""); // 直接處理刪除
                            GUIEditor.openCommandManager(player, gui, itemSlot, pageId, commandPage);
                        }
                    }
                }
            }
        } else if (holder instanceof GUIHolder guiHolder) {
            CustomGUI gui = guiHolder.getGUI();
            int pageId = guiHolder.getPageId();
            if (!gui.pages().containsKey(pageId)) {
                gui.pages().put(pageId, new GUIPage());
                plugin.getLogger().warning(String.format(LOG_GUI_MISSING_PAGE, gui.name(), pageId));
                plugin.getDataStorage().saveGUIsAsync();
            }
            GUIPage page = gui.pages().get(pageId);
            GUIItem item = page.items().get(slot);
            // 觸發 GUIClickEvent
            GUIClickEvent clickEvent = new GUIClickEvent(player, gui, pageId, slot, item);
            plugin.getServer().getPluginManager().callEvent(clickEvent);
            if (clickEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            // 移除硬編碼的欄位 0 邏輯，依賴 GUIClickEvent 處理
            if (page.allowInteract()) {
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
            String input = Arrays.stream(event.getLines())
                    .filter(line -> line != null && !line.trim().isEmpty())
                    .collect(Collectors.joining(" ")).trim();
            plugin.getLogger().info("玩家 " + player.getName() + " 輸入告示牌內容: " + input + "，狀態: " + session.state());
            inputHandler.removeSign(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    String newGuiName = session.handleInput(player, input, plugin);
                    if (newGuiName != null) {
                        plugin.getDataStorage().saveGUIsAsync();
                        CustomGUI updatedGui = plugin.getGuiManager().getGUI(newGuiName);
                        if (updatedGui != null) {
                            if (!updatedGui.pages().containsKey(session.pageId())) {
                                updatedGui.pages().put(session.pageId(), new GUIPage());
                                plugin.getLogger().warning("GUI " + newGuiName + " 缺少 pageId=" + session.pageId() + "，已自動創建");
                                plugin.getDataStorage().saveGUIsAsync();
                            }
                            GUIEditor.openEditor(player, updatedGui, session.pageId());
                        } else if (session.state().equals("create_gui")) {
                            CustomGUI newGui = plugin.getGuiManager().getGUI(newGuiName);
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        inputHandler.removeSign(player);
        plugin.setEditSession(player.getUniqueId(), null);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = inputHandler.getActiveSigns().get(player);
        if (loc != null && player.getLocation().distance(loc) > 5) {
            inputHandler.removeSign(player);
            plugin.setEditSession(player.getUniqueId(), null);
            player.sendMessage("§c輸入取消：移動過遠");
        }
    }

    private void handleInput(Player player, String input) {
        EditSession session = plugin.getEditSession(player.getUniqueId());
        if (session == null) {
            player.sendMessage("§c輸入會話已過期，請重新操作！");
            inputHandler.removeSign(player);
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                String newGuiName = session.handleInput(player, input, plugin);
                inputHandler.removeSign(player);
                if (newGuiName != null) {
                    plugin.getDataStorage().saveGUIsAsync();
                    CustomGUI updatedGui = plugin.getGuiManager().getGUI(newGuiName);
                    if (updatedGui != null) {
                        if (!updatedGui.pages().containsKey(session.pageId())) {
                            updatedGui.pages().put(session.pageId(), new GUIPage());
                            plugin.getLogger().warning("GUI " + newGuiName + " 缺少 pageId=" + session.pageId() + "，已自動創建");
                            plugin.getDataStorage().saveGUIsAsync();
                        }
                        GUIEditor.openEditor(player, updatedGui, session.pageId());
                    } else if (session.state().equals("create_gui")) {
                        CustomGUI newGui = plugin.getGuiManager().getGUI(newGuiName);
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