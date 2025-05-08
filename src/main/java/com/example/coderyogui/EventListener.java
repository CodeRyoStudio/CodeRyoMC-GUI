package com.example.coderyogui;

import org.bukkit.ChatColor;
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
        LanguageManager lang = plugin.getLanguageManager();

        if (rawSlot != slot || rawSlot >= event.getInventory().getSize()) {
            return;
        }

        if (holder instanceof MainMenuHolder menuHolder) {
            event.setCancelled(true);
            plugin.getLogger().info("Player " + player.getName() + " clicked main menu, slot " + slot);
            if (slot == 0) {
                if (!player.hasPermission("coderyogui.create")) {
                    player.sendMessage(lang.getTranslation(player, "message.no_permission_create"));
                    return;
                }
                plugin.setEditSession(player.getUniqueId(), new EditSession(null, "create_gui", -1, 1));
                player.closeInventory();
                inputHandler.openSignInput(player, "create_gui", input -> handleInput(player, input));
            } else if (slot == 1) {
                if (!player.hasPermission("coderyogui.use")) {
                    player.sendMessage(lang.getTranslation(player, "message.no_permission_use"));
                    return;
                }
                new GUIListGUI(plugin, 1, false, null).open(player);
            } else if (slot == 2) {
                if (!player.hasPermission("coderyogui.edit.*")) {
                    player.sendMessage(lang.getTranslation(player, "message.no_permission_edit"));
                    return;
                }
                new GUIListGUI(plugin, 1, true, null).open(player);
            } else if (slot == 3) {
                if (!player.hasPermission("coderyogui.del")) {
                    player.sendMessage(lang.getTranslation(player, "message.no_permission_delete"));
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
                            player.sendMessage(lang.getTranslation(player, "message.no_permission_use"));
                            return;
                        }
                        CustomGUI gui = plugin.getGuiManager().getGUI(guiName);
                        if (gui != null) {
                            player.openInventory(gui.getPage(1));
                            player.sendMessage(lang.getTranslation(player, "message.gui_opened", guiName));
                        } else {
                            player.sendMessage(lang.getTranslation(player, "message.gui_not_found"));
                        }
                    } else if (event.getClick() == ClickType.RIGHT) {
                        if (!player.hasPermission("coderyogui.edit." + guiName)) {
                            player.sendMessage(lang.getTranslation(player, "message.no_permission_edit_specific", guiName));
                            return;
                        }
                        CustomGUI gui = plugin.getGuiManager().getGUI(guiName);
                        if (gui != null) {
                            GUIEditor.openEditor(player, gui, 1);
                            player.sendMessage(lang.getTranslation(player, "message.editing_gui", guiName));
                        } else {
                            player.sendMessage(lang.getTranslation(player, "message.gui_not_found"));
                        }
                    }
                }
            } else if (slot == 52 && menuHolder.getPage() > 1) {
                new MainMenuGUI(plugin, menuHolder.getPage() - 1).open(player);
                player.sendMessage(lang.getTranslation(player, "message.page_switched_prev"));
            } else if (slot == 53) {
                new MainMenuGUI(plugin, menuHolder.getPage() + 1).open(player);
                player.sendMessage(lang.getTranslation(player, "message.page_switched_next"));
            }
        } else if (holder instanceof GUIListHolder listHolder) {
            event.setCancelled(true);
            plugin.getLogger().info("Player " + player.getName() + " clicked GUI list, slot " + slot + ", mode: " + (listHolder.isEditMode() ? "edit" : "open"));
            if (slot == 0) {
                new MainMenuGUI(plugin, 1).open(player);
                player.sendMessage(lang.getTranslation(player, "message.returned_main_menu"));
            } else if (slot == 1) {
                plugin.setEditSession(player.getUniqueId(), new EditSession(null, "search_gui", -1, listHolder.getPage(), new ArrayList<>(Collections.singletonList(listHolder.isEditMode() ? "edit" : "open"))));
                player.closeInventory();
                inputHandler.openSignInput(player, "search_gui", input -> handleInput(player, input));
            } else if (slot >= 9 && slot < 54) {
                List<String> guiNames = new ArrayList<>(plugin.getGuiManager().getGUIs().keySet()).stream()
                        .filter(name -> listHolder.getSearch() == null || name.toLowerCase().contains(listHolder.getSearch().toLowerCase()))
                        .collect(Collectors.toList());
                int index = (listHolder.getPage() - 1) * 45 + (slot - 9);
                if (index < guiNames.size()) {
                    String guiName = guiNames.get(index);
                    CustomGUI gui = plugin.getGuiManager().getGUI(guiName);
                    if (gui == null) {
                        player.sendMessage(lang.getTranslation(player, "message.gui_not_found"));
                        return;
                    }
                    if (listHolder.isEditMode()) {
                        if (!player.hasPermission("coderyogui.edit." + guiName)) {
                            player.sendMessage(lang.getTranslation(player, "message.no_permission_edit_specific", guiName));
                            return;
                        }
                        GUIEditor.openEditor(player, gui, 1);
                        player.sendMessage(lang.getTranslation(player, "message.editing_gui", guiName));
                    } else {
                        if (!player.hasPermission("coderyogui.use")) {
                            player.sendMessage(lang.getTranslation(player, "message.no_permission_use"));
                            return;
                        }
                        player.openInventory(gui.getPage(1));
                        player.sendMessage(lang.getTranslation(player, "message.gui_opened", guiName));
                    }
                }
            } else if (slot == 52 && listHolder.getPage() > 1) {
                new GUIListGUI(plugin, listHolder.getPage() - 1, listHolder.isEditMode(), listHolder.getSearch()).open(player);
                player.sendMessage(lang.getTranslation(player, "message.page_switched_prev"));
            } else if (slot == 53) {
                new GUIListGUI(plugin, listHolder.getPage() + 1, listHolder.isEditMode(), listHolder.getSearch()).open(player);
                player.sendMessage(lang.getTranslation(player, "message.page_switched_next"));
            }
        } else if (holder instanceof DeleteGUIListHolder deleteHolder) {
            event.setCancelled(true);
            plugin.getLogger().info("Player " + player.getName() + " clicked delete GUI list, slot " + slot);
            if (slot == 0) {
                new MainMenuGUI(plugin, 1).open(player);
                player.sendMessage(lang.getTranslation(player, "message.returned_main_menu"));
            } else if (slot == 1) {
                plugin.setEditSession(player.getUniqueId(), new EditSession(null, "search_gui", -1, deleteHolder.getPage(), new ArrayList<>(Collections.singletonList("delete"))));
                player.closeInventory();
                inputHandler.openSignInput(player, "search_gui", input -> handleInput(player, input));
            } else if (slot >= 9 && slot < 54) {
                List<String> guiNames = new ArrayList<>(plugin.getGuiManager().getGUIs().keySet()).stream()
                        .filter(name -> deleteHolder.getSearch() == null || name.toLowerCase().contains(deleteHolder.getSearch().toLowerCase()))
                        .collect(Collectors.toList());
                int index = (deleteHolder.getPage() - 1) * 45 + (slot - 9);
                if (index < guiNames.size()) {
                    String guiName = guiNames.get(index);
                    if (!player.hasPermission("coderyogui.del")) {
                        player.sendMessage(lang.getTranslation(player, "message.no_permission_delete"));
                        return;
                    }
                    DeleteGUIListGUI.openConfirm(player, guiName);
                }
            } else if (slot == 52 && deleteHolder.getPage() > 1) {
                new DeleteGUIListGUI(plugin, deleteHolder.getPage() - 1, deleteHolder.getSearch()).open(player);
                player.sendMessage(lang.getTranslation(player, "message.page_switched_prev"));
            } else if (slot == 53) {
                new DeleteGUIListGUI(plugin, deleteHolder.getPage() + 1, deleteHolder.getSearch()).open(player);
                player.sendMessage(lang.getTranslation(player, "message.page_switched_next"));
            }
        } else if (holder instanceof DeleteConfirmHolder confirmHolder) {
            event.setCancelled(true);
            plugin.getLogger().info("Player " + player.getName() + " clicked delete confirm, slot " + slot);
            if (slot == 3) {
                String guiName = confirmHolder.getGuiName();
                plugin.getGuiManager().getGUIs().remove(guiName);
                plugin.getDataStorage().saveGUIsAsync();
                player.sendMessage(lang.getTranslation(player, "command.deleted_gui", guiName));
                new MainMenuGUI(plugin, 1).open(player);
            } else if (slot == 5) {
                new MainMenuGUI(plugin, 1).open(player);
                player.sendMessage(lang.getTranslation(player, "message.delete_cancelled"));
            }
        } else if (holder instanceof EditorHolder editorHolder) {
            CustomGUI gui = editorHolder.getGUI();
            int pageId = editorHolder.getPageId();
            GUIPage page = gui != null ? gui.pages().get(pageId) : null;

            if (title.startsWith(ChatColor.stripColor(lang.getTranslation(player, "gui.title.edit", "")))) {
                event.setCancelled(true);
                if (gui == null || page == null) {
                    player.sendMessage(lang.getTranslation(player, "message.invalid_gui"));
                    new MainMenuGUI(plugin, 1).open(player);
                    return;
                }
                if (slot == 0) {
                    new MainMenuGUI(plugin, 1).open(player);
                    player.sendMessage(lang.getTranslation(player, "message.returned_main_menu"));
                } else if (slot == 1) {
                    plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_name", -1, pageId));
                    player.closeInventory();
                    inputHandler.openSignInput(player, "set_gui_name", input -> handleInput(player, input));
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
                        GUIEditor.openContextMenu(player, gui, slot - 9, pageId);
                    }
                }
            } else if (title.equals(lang.getTranslation(player, "gui.title.item_settings"))) {
                event.setCancelled(true);
                if (gui == null) {
                    player.sendMessage(lang.getTranslation(player, "message.invalid_gui"));
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
                        inputHandler.openSignInput(player, "set_player_command", input -> handleInput(player, input));
                        break;
                    case 2:
                        plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_command", itemSlot, pageId, new ArrayList<>(Collections.singletonList("console"))));
                        player.closeInventory();
                        inputHandler.openSignInput(player, "set_console_command", input -> handleInput(player, input));
                        break;
                    case 3:
                        plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "set_item_name", itemSlot, pageId));
                        player.closeInventory();
                        inputHandler.openSignInput(player, "set_item_name", input -> handleInput(player, input));
                        break;
                    case 4:
                        GUIEditor.openEditor(player, gui, pageId);
                        break;
                }
            } else if (title.startsWith(lang.getTranslation(player, "gui.title.select_item")) || title.startsWith(lang.getTranslation(player, "gui.title.search_item", ""))) {
                event.setCancelled(true);
                if (gui == null) {
                    player.sendMessage(lang.getTranslation(player, "message.invalid_gui"));
                    new MainMenuGUI(plugin, 1).open(player);
                    return;
                }
                String search = editorHolder.getSearch();
                int searchPage = editorHolder.getSearchPage();
                plugin.getLogger().info("Player " + player.getName() + " clicked item select list, slot " + slot + ", search: " + (search != null ? search : "none") + ", page: " + searchPage);
                if (slot == 0) {
                    GUIEditor.openContextMenu(player, gui, editorHolder.getSlot(), pageId);
                } else if (slot == 1) {
                    plugin.setEditSession(player.getUniqueId(), new EditSession(gui, "search_item", editorHolder.getSlot(), pageId));
                    player.closeInventory();
                    inputHandler.openSignInput(player, "search_item", input -> handleInput(player, input));
                } else if (slot == 52) {
                    if (searchPage > 1) {
                        GUIEditor.openItemSelect(player, gui, editorHolder.getSlot(), pageId, search, searchPage - 1);
                        player.sendMessage(lang.getTranslation(player, "message.page_switched_prev"));
                    } else {
                        player.sendMessage(lang.getTranslation(player, "message.first_page"));
                    }
                } else if (slot == 53) {
                    GUIEditor.openItemSelect(player, gui, editorHolder.getSlot(), pageId, search, searchPage + 1);
                    player.sendMessage(lang.getTranslation(player, "message.page_switched_next"));
                } else if (slot >= 9 && slot < 52) {
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
                    plugin.getLogger().info("Item selection: slot " + slot + ", index " + index + ", materials count " + materials.size());
                    if (index < materials.size()) {
                        Material material = materials.get(index);
                        page.items().put(editorHolder.getSlot(), new GUIItem(material.name(), null, null, true, new ArrayList<>()));
                        gui.pages().put(pageId, page);
                        plugin.getGuiManager().getGUIs().put(gui.name(), gui);
                        plugin.getDataStorage().saveGUIsAsync();
                        GUIEditor.openContextMenu(player, gui, editorHolder.getSlot(), pageId);
                    }
                }
            } else if (title.equals(lang.getTranslation(player, "gui.title.select_rows"))) {
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
            GUIPage page = gui.pages().get(guiHolder.getPageId());
            GUIItem item = page.items().get(slot);
            if (slot == 0) {
                event.setCancelled(true);
                new MainMenuGUI(plugin, 1).open(player);
                player.sendMessage(lang.getTranslation(player, "message.returned_main_menu"));
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
        LanguageManager lang = plugin.getLanguageManager();
        if (session != null) {
            String input = Arrays.stream(event.getLines())
                    .filter(line -> line != null && !line.trim().isEmpty())
                    .collect(Collectors.joining(" ")).trim();
            plugin.getLogger().info("Player " + player.getName() + " entered sign input: " + input + ", state: " + session.state());
            new BukkitRunnable() {
                @Override
                public void run() {
                    String newGuiName = session.handleInput(player, input, plugin);
                    if (newGuiName != null) {
                        plugin.getDataStorage().saveGUIsAsync();
                        CustomGUI updatedGui = plugin.getGuiManager().getGUI(newGuiName);
                        if (updatedGui != null) {
                            GUIEditor.openEditor(player, updatedGui, session.pageId());
                        } else if (session.state().equals("create_gui")) {
                            GUIEditor.openEditor(player, plugin.getGuiManager().getGUI(newGuiName), 1);
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
                            player.sendMessage(lang.getTranslation(player, "message.gui_update_failed"));
                        }
                        plugin.setEditSession(player.getUniqueId(), null);
                    } else {
                        player.sendMessage(lang.getTranslation(player, "message.invalid_input"));
                        inputHandler.openSignInput(player, "retry_input", input -> handleInput(player, input));
                    }
                }
            }.runTask(plugin);
        }
    }

    private void handleInput(Player player, String input) {
        EditSession session = plugin.getEditSession(player.getUniqueId());
        LanguageManager lang = plugin.getLanguageManager();
        if (session == null) {
            player.sendMessage(lang.getTranslation(player, "message.input_session_expired"));
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
                        GUIEditor.openEditor(player, updatedGui, session.pageId());
                    } else if (session.state().equals("create_gui")) {
                        GUIEditor.openEditor(player, plugin.getGuiManager().getGUI(newGuiName), 1);
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
                        player.sendMessage(lang.getTranslation(player, "message.gui_update_failed"));
                    }
                    plugin.setEditSession(player.getUniqueId(), null);
                } else {
                    player.sendMessage(lang.getTranslation(player, "message.invalid_input"));
                    inputHandler.openSignInput(player, "retry_input", input -> handleInput(player, input));
                }
            }
        }.runTask(plugin);
    }
}