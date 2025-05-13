package com.coderyo.coderyogui;

import com.coderyo.coderyogui.api.StringSanitizer;
import com.coderyo.coderyogui.api.ActionType; // Added import
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public record EditSession(CustomGUI gui, String state, int slot, int pageId, List<String> tempData) {
    public EditSession(CustomGUI gui, String state, int slot, int pageId) {
        this(gui, state, slot, pageId, new ArrayList<>());
    }

    public String handleInput(Player player, String input, CoderyoGUI plugin) {
        if (input != null && input.length() > 100) {
            if (player != null) {
                player.sendMessage("§cInput exceeds 100 characters!");
            }
            plugin.getLogger().warning("Input too long: " + input);
            return null;
        }
        String sanitizedInput = StringSanitizer.sanitize(input);
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI currentGui = gui;
        GUIPage page = currentGui != null ? currentGui.pages().get(pageId) : null;
        switch (state) {
            case "create_gui":
                if (guiManager.getGUIs().containsKey(sanitizedInput) || guiManager.getTemporaryGUIs().containsKey(sanitizedInput)) {
                    if (player != null) {
                        player.sendMessage("§cGUI name already exists!");
                    }
                    plugin.getLogger().warning("GUI name exists: " + sanitizedInput);
                    return null;
                }
                if (sanitizedInput == null || sanitizedInput.isEmpty() || sanitizedInput.length() > 32) {
                    if (player != null) {
                        player.sendMessage("§cName must be 1-32 characters!");
                    }
                    plugin.getLogger().warning("Invalid GUI name: " + sanitizedInput);
                    return null;
                }
                CustomGUI newGui = new CustomGUI(sanitizedInput, 3);
                guiManager.getGUIs().put(sanitizedInput, newGui);
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage("§aGUI " + sanitizedInput + " created!");
                }
                plugin.getLogger().info("Created GUI: " + sanitizedInput);
                return sanitizedInput;
            case "set_name":
                if (sanitizedInput == null || sanitizedInput.isEmpty() || sanitizedInput.length() > 32) {
                    if (player != null) {
                        player.sendMessage("§cName must be 1-32 characters!");
                    }
                    plugin.getLogger().warning("Invalid GUI name: " + sanitizedInput);
                    return null;
                }
                if (guiManager.getGUIs().containsKey(sanitizedInput) && !sanitizedInput.equals(currentGui.name())) {
                    if (player != null) {
                        player.sendMessage("§cGUI name already exists!");
                    }
                    plugin.getLogger().warning("GUI name exists: " + sanitizedInput);
                    return null;
                }
                guiManager.getGUIs().remove(currentGui.name());
                CustomGUI updatedGui = new CustomGUI(sanitizedInput, currentGui.rows(), currentGui.pages());
                if (guiManager.isTemporary(currentGui.name())) {
                    guiManager.getTemporaryGUIs().remove(currentGui.name());
                    guiManager.getTemporaryGUIs().put(sanitizedInput, updatedGui);
                } else {
                    guiManager.getGUIs().put(sanitizedInput, updatedGui);
                }
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage("§aGUI name updated to " + sanitizedInput + "!");
                }
                plugin.getLogger().info("Updated GUI name to: " + sanitizedInput);
                if (!guiManager.isTemporary(sanitizedInput)) {
                    plugin.getDataStorage().saveGUIsAsync();
                }
                return sanitizedInput;
            case "set_item_id":
                if (Material.matchMaterial(sanitizedInput) == null) {
                    if (player != null) {
                        player.sendMessage("§cInvalid material ID: " + sanitizedInput);
                    }
                    plugin.getLogger().warning("Invalid material: " + sanitizedInput);
                    return null;
                }
                page.items().put(slot, new GUIItem(sanitizedInput, null, new ArrayList<>(), true, new ArrayList<>()));
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                plugin.getLogger().info("Set material: " + sanitizedInput + " at slot: " + slot);
                return currentGui.name();
            case "set_item_name":
                if (page.items().get(slot) == null) {
                    if (player != null) {
                        player.sendMessage("§cNo item at slot " + slot + "!");
                    }
                    plugin.getLogger().warning("No item at slot " + slot + " in GUI " + currentGui.name());
                    return null;
                }
                GUIItem item = page.items().get(slot);
                page.items().put(slot, new GUIItem(item.material(), sanitizedInput, item.lore(), item.takeable(), item.actions()));
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                plugin.getLogger().info("Set item name: " + sanitizedInput + " at slot: " + slot);
                return currentGui.name();
            case "set_lore":
                item = page.items().get(slot);
                if (item == null) {
                    if (player != null) {
                        player.sendMessage("§cNo item at slot " + slot + "!");
                    }
                    plugin.getLogger().warning("No item at slot " + slot + " in GUI " + currentGui.name());
                    return null;
                }
                List<String> lore = new ArrayList<>(item.lore());
                lore.add(sanitizedInput);
                page.items().put(slot, new GUIItem(item.material(), item.name(), lore, item.takeable(), item.actions()));
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                plugin.getLogger().info("Added lore line: " + sanitizedInput + " at slot: " + slot);
                return currentGui.name();
            case "set_command":
                item = page.items().get(slot);
                List<GUIAction> actions;
                String material;
                String itemName;
                List<String> loreList;
                boolean takeable;
                if (item == null) {
                    plugin.getLogger().warning("No item at slot " + slot + ", creating default GUIItem");
                    material = "AIR";
                    itemName = null;
                    loreList = new ArrayList<>();
                    takeable = true;
                    actions = new ArrayList<>();
                } else {
                    material = item.material();
                    itemName = item.name();
                    loreList = item.lore();
                    takeable = item.takeable();
                    actions = new ArrayList<>(item.actions());
                }
                String command = sanitizedInput != null && sanitizedInput.startsWith("/") ? sanitizedInput.substring(1) : sanitizedInput;
                boolean asConsole = tempData != null && tempData.contains("console");
                actions.add(new GUIAction(ActionType.COMMAND, command, asConsole));
                page.items().put(slot, new GUIItem(material, itemName, loreList, takeable, actions));
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage("§aCommand set!");
                }
                plugin.getLogger().info("Set command: " + command + ", asConsole: " + asConsole + ", at slot: " + slot);
                return currentGui.name();
            case "set_message_action":
                item = page.items().get(slot);
                if (item == null) {
                    if (player != null) {
                        player.sendMessage("§cNo item at slot " + slot + "!");
                    }
                    plugin.getLogger().warning("No item at slot " + slot + " in GUI " + currentGui.name());
                    return null;
                }
                actions = new ArrayList<>(item.actions());
                actions.add(new GUIAction(ActionType.MESSAGE, sanitizedInput));
                page.items().put(slot, new GUIItem(item.material(), item.name(), item.lore(), item.takeable(), actions));
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage("§aMessage action set!");
                }
                plugin.getLogger().info("Set message: " + sanitizedInput + " at slot: " + slot);
                return currentGui.name();
            case "set_sound_action":
                item = page.items().get(slot);
                if (item == null) {
                    if (player != null) {
                        player.sendMessage("§cNo item at slot " + slot + "!");
                    }
                    plugin.getLogger().warning("No item at slot " + slot + " in GUI " + currentGui.name());
                    return null;
                }
                try {
                    Sound.valueOf(sanitizedInput.toUpperCase());
                } catch (IllegalArgumentException e) {
                    if (player != null) {
                        player.sendMessage("§cInvalid sound: " + sanitizedInput);
                    }
                    plugin.getLogger().warning("Invalid sound: " + sanitizedInput);
                    return null;
                }
                actions = new ArrayList<>(item.actions());
                actions.add(new GUIAction(ActionType.SOUND, sanitizedInput));
                page.items().put(slot, new GUIItem(item.material(), item.name(), item.lore(), item.takeable(), actions));
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage("§aSound action set!");
                }
                plugin.getLogger().info("Set sound: " + sanitizedInput + " at slot: " + slot);
                return currentGui.name();
            case "set_close_action":
                item = page.items().get(slot);
                if (item == null) {
                    if (player != null) {
                        player.sendMessage("§cNo item at slot " + slot + "!");
                    }
                    plugin.getLogger().warning("No item at slot " + slot + " in GUI " + currentGui.name());
                    return null;
                }
                actions = new ArrayList<>(item.actions());
                actions.add(new GUIAction(ActionType.CLOSE, "close"));
                page.items().put(slot, new GUIItem(item.material(), item.name(), item.lore(), item.takeable(), actions));
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage("§aClose action set!");
                }
                plugin.getLogger().info("Set close action at slot: " + slot);
                return currentGui.name();
            case "set_takeable":
                item = page.items().get(slot);
                if (item == null) {
                    if (player != null) {
                        player.sendMessage("§cNo item at slot " + slot + "!");
                    }
                    plugin.getLogger().warning("No item at slot " + slot + " in GUI " + currentGui.name());
                    return null;
                }
                boolean takeableValue;
                try {
                    takeableValue = Boolean.parseBoolean(sanitizedInput);
                } catch (Exception e) {
                    if (player != null) {
                        player.sendMessage("§cInvalid takeable value: " + sanitizedInput);
                    }
                    plugin.getLogger().warning("Invalid takeable value: " + sanitizedInput);
                    return null;
                }
                page.items().put(slot, new GUIItem(item.material(), item.name(), item.lore(), takeableValue, item.actions()));
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                plugin.getLogger().info("Set takeable: " + takeableValue + " at slot: " + slot);
                return currentGui.name();
            case "set_page_interact":
                boolean allowInteract;
                try {
                    allowInteract = Boolean.parseBoolean(sanitizedInput);
                } catch (Exception e) {
                    if (player != null) {
                        player.sendMessage("§cInvalid interact value: " + sanitizedInput);
                    }
                    plugin.getLogger().warning("Invalid interact value: " + sanitizedInput);
                    return null;
                }
                page = new GUIPage(page.items(), allowInteract);
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage("§aPage interact set to: " + allowInteract);
                }
                plugin.getLogger().info("Set page interact: " + allowInteract + " for page: " + pageId);
                return currentGui.name();
            case "add_page":
                try {
                    int newPageId = Integer.parseInt(sanitizedInput);
                    if (newPageId < 1 || currentGui.pages().containsKey(newPageId)) {
                        if (player != null) {
                            player.sendMessage("§cInvalid or existing page ID: " + newPageId);
                        }
                        plugin.getLogger().warning("Invalid page ID: " + newPageId);
                        return null;
                    }
                    currentGui.pages().put(newPageId, new GUIPage());
                    guiManager.getGUIs().put(currentGui.name(), currentGui);
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                        player.sendMessage("§aPage " + newPageId + " added!");
                    }
                    plugin.getLogger().info("Added page: " + newPageId + " to GUI: " + currentGui.name());
                    return currentGui.name();
                } catch (NumberFormatException e) {
                    if (player != null) {
                        player.sendMessage("§cInvalid page ID: " + sanitizedInput);
                    }
                    plugin.getLogger().warning("Invalid page ID: " + sanitizedInput);
                    return null;
                }
            case "remove_page":
                try {
                    int removePageId = Integer.parseInt(sanitizedInput);
                    if (!currentGui.pages().containsKey(removePageId)) {
                        if (player != null) {
                            player.sendMessage("§cPage not found: " + removePageId);
                        }
                        plugin.getLogger().warning("Page not found: " + removePageId);
                        return null;
                    }
                    if (currentGui.pages().size() <= 1) {
                        if (player != null) {
                            player.sendMessage("§cCannot remove the last page!");
                        }
                        plugin.getLogger().warning("Cannot remove last page in GUI: " + currentGui.name());
                        return null;
                    }
                    currentGui.pages().remove(removePageId);
                    guiManager.getGUIs().put(currentGui.name(), currentGui);
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                        player.sendMessage("§aPage " + removePageId + " removed!");
                    }
                    plugin.getLogger().info("Removed page: " + removePageId + " from GUI: " + currentGui.name());
                    return currentGui.name();
                } catch (NumberFormatException e) {
                    if (player != null) {
                        player.sendMessage("§cInvalid page ID: " + sanitizedInput);
                    }
                    plugin.getLogger().warning("Invalid page ID: " + sanitizedInput);
                    return null;
                }
            case "delete_item":
                page.items().remove(slot);
                currentGui.pages().put(pageId, page);
                guiManager.getGUIs().put(currentGui.name(), currentGui);
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                    player.sendMessage("§aItem removed!");
                }
                plugin.getLogger().info("Removed item at slot: " + slot + " in GUI: " + currentGui.name());
                return currentGui.name();
            case "delete_gui":
                if (guiManager.getGUIs().remove(sanitizedInput) != null || guiManager.getTemporaryGUIs().remove(sanitizedInput) != null) {
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                        player.sendMessage("§aGUI " + sanitizedInput + " deleted!");
                    }
                    plugin.getLogger().info("Deleted GUI: " + sanitizedInput);
                    return sanitizedInput;
                }
                if (player != null) {
                    player.sendMessage("§cGUI not found: " + sanitizedInput);
                }
                plugin.getLogger().warning("GUI not found: " + sanitizedInput);
                return null;
            case "edit_command":
            case "delete_command":
                item = page.items().get(slot);
                if (item == null || item.actions().isEmpty()) {
                    if (player != null) {
                        player.sendMessage("§cNo commands to " + (state.equals("edit_command") ? "edit" : "delete") + "!");
                    }
                    plugin.getLogger().warning("No commands at slot " + slot + " in GUI " + currentGui.name());
                    return null;
                }
                try {
                    int commandIndex = Integer.parseInt(tempData.get(0));
                    actions = new ArrayList<>(item.actions());
                    if (state.equals("edit_command")) {
                        GUIAction oldAction = actions.get(commandIndex);
                        String newCommand = sanitizedInput != null && sanitizedInput.startsWith("/") ? sanitizedInput.substring(1) : sanitizedInput;
                        actions.set(commandIndex, new GUIAction(ActionType.COMMAND, newCommand, oldAction.asConsole()));
                        if (player != null) {
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                            player.sendMessage("§aCommand updated!");
                        }
                        plugin.getLogger().info("Updated command: " + newCommand + " at slot: " + slot);
                    } else {
                        actions.remove(commandIndex);
                        if (player != null) {
                            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                            player.sendMessage("§aCommand deleted!");
                        }
                        plugin.getLogger().info("Deleted command at slot: " + slot);
                    }
                    page.items().put(slot, new GUIItem(item.material(), item.name(), item.lore(), item.takeable(), actions));
                    currentGui.pages().put(pageId, page);
                    guiManager.getGUIs().put(currentGui.name(), currentGui);
                    return currentGui.name();
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    if (player != null) {
                        player.sendMessage("§cInvalid command index!");
                    }
                    plugin.getLogger().warning("Invalid command index: " + e.getMessage());
                    return null;
                }
            case "search_item":
            case "search_gui":
                return sanitizedInput;
            default:
                plugin.getLogger().warning("Unknown state: " + state);
                return null;
        }
    }
}