# CoderyoGUI API Documentation

## Overview

The `CoderyoGUI` plugin provides a powerful API for creating, editing, deleting, and opening custom GUIs in Minecraft servers running on Paper/Spigot. The API is designed to integrate seamlessly with the plugin's built-in features, allowing other plugins to manage GUIs programmatically while sharing the same data storage (`guis.yml`). This document explains how to use the API, including the `CoderyoGUIAPI` class, `GUIItemBuilder` utility, and `GUIClickEvent` for handling GUI interactions.

## Getting Started

To use the `CoderyoGUI` API, ensure the `CoderyoGUI` plugin is installed on your server and listed as a dependency in your plugin's `plugin.yml`:

```yaml
depend: [CoderyoGUI]
# or
softdepend: [CoderyoGUI]
```

The API is located in the `com.coderyo.coderyogui.api` package. Import the necessary classes:

```java
import com.coderyo.coderyogui.api.CoderyoGUIAPI;
import com.coderyo.coderyogui.api.GUIItemBuilder;
import com.coderyo.coderyogui.api.GUIClickEvent;
```

## Accessing the API

The `CoderyoGUIAPI` class is the main entry point. Obtain an instance using:

```java
CoderyoGUIAPI api = CoderyoGUIAPI.getInstance();
```

**Note**: Ensure `CoderyoGUI` is enabled before calling `getInstance()`. If the plugin is not initialized, an `IllegalStateException` will be thrown.

## API Methods

### 1. Creating a GUI
```java
boolean createGUI(String name, int rows)
```
- **Purpose**: Creates a new GUI with the specified name and number of rows.
- **Parameters**:
  - `name`: The unique name of the GUI (1-32 characters, non-null, non-empty).
  - `rows`: The number of rows (1-6).
- **Returns**: `true` if successful, `false` if the name is invalid, rows are out of range, or the GUI already exists.
- **Example**:
  ```java
  api.createGUI("reward_gui", 3); // Creates a 3-row GUI named "reward_gui"
  ```

### 2. Modifying GUI Rows
```java
boolean setGUIRows(String name, int rows)
```
- **Purpose**: Updates the number of rows for an existing GUI.
- **Parameters**:
  - `name`: The GUI name.
  - `rows`: The new number of rows (1-6).
- **Returns**: `true` if successful, `false` if the GUI doesn't exist or rows are invalid.
- **Example**:
  ```java
  api.setGUIRows("reward_gui", 4); // Changes to 4 rows
  ```

### 3. Adding a Page
```java
boolean addPage(String name, int pageId)
```
- **Purpose**: Adds a new page to the GUI.
- **Parameters**:
  - `name`: The GUI name.
  - `pageId`: The page ID (positive integer, must not already exist).
- **Returns**: `true` if successful, `false` if the GUI doesn't exist or pageId is invalid/exists.
- **Example**:
  ```java
  api.addPage("reward_gui", 2); // Adds page 2
  ```

### 4. Removing a Page
```java
boolean removePage(String name, int pageId)
```
- **Purpose**: Removes a page from the GUI (GUI must retain at least one page).
- **Parameters**:
  - `name`: The GUI name.
  - `pageId`: The page ID to remove.
- **Returns**: `true` if successful, `false` if the GUI doesn't exist, pageId doesn't exist, or it's the last page.
- **Example**:
  ```java
  api.removePage("reward_gui", 2); // Removes page 2
  ```

### 5. Setting an Item
```java
boolean setItem(String name, int pageId, int slot, GUIItem item)
```
- **Purpose**: Places a `GUIItem` in a specific slot on a GUI page.
- **Parameters**:
  - `name`: The GUI name.
  - `pageId`: The page ID.
  - `slot`: The slot index (9 to `rows * 9 - 1`).
  - `item`: The `GUIItem` to place (non-null).
- **Returns**: `true` if successful, `false` if the GUI, page, or slot is invalid, or item is null.
- **Example**:
  ```java
  GUIItem item = new GUIItemBuilder().material("DIAMOND").build();
  api.setItem("reward_gui", 1, 10, item); // Places a diamond in slot 10
  ```

### 6. Removing an Item
```java
boolean removeItem(String name, int pageId, int slot)
```
- **Purpose**: Removes an item from a specific slot.
- **Parameters**:
  - `name`: The GUI name.
  - `pageId`: The page ID.
  - `slot`: The slot index (9 to `rows * 9 - 1`).
- **Returns**: `true` if successful, `false` if the GUI, page, or slot is invalid.
- **Example**:
  ```java
  api.removeItem("reward_gui", 1, 10); // Removes item from slot 10
  ```

### 7. Setting Page Interactability
```java
boolean setPageInteractable(String name, int pageId, boolean allowInteract)
```
- **Purpose**: Sets whether a page allows player interactions (e.g., taking items).
- **Parameters**:
  - `name`: The GUI name.
  - `pageId`: The page ID.
  - `allowInteract`: `true` to allow interactions, `false` to prevent them.
- **Returns**: `true` if successful, `false` if the GUI or page doesn't exist.
- **Example**:
  ```java
  api.setPageInteractable("reward_gui", 1, false); // Disables interactions
  ```

### 8. Deleting a GUI
```java
boolean deleteGUI(String name)
```
- **Purpose**: Deletes a GUI.
- **Parameters**:
  - `name`: The GUI name.
- **Returns**: `true` if successful, `false` if the GUI doesn't exist.
- **Example**:
  ```java
  api.deleteGUI("reward_gui"); // Deletes the GUI
  ```

### 9. Opening a GUI
```java
boolean openGUI(Player player, String name, int pageId)
```
- **Purpose**: Opens a GUI page for a player.
- **Parameters**:
  - `player`: The player (non-null).
  - `name`: The GUI name.
  - `pageId`: The page ID.
- **Returns**: `true` if successful, `false` if the player, GUI, or page is invalid.
- **Example**:
  ```java
  api.openGUI(player, "reward_gui", 1); // Opens page 1 for the player
  ```

### 10. Getting a GUI
```java
CustomGUI getGUI(String name)
```
- **Purpose**: Retrieves a read-only copy of a GUI.
- **Parameters**:
  - `name`: The GUI name.
- **Returns**: The `CustomGUI` object or `null` if the GUI doesn't exist.
- **Example**:
  ```java
  CustomGUI gui = api.getGUI("reward_gui");
  if (gui != null) {
      // Process GUI data
  }
  ```

### 11. Listing GUIs
```java
Set<String> listGUIs()
```
- **Purpose**: Returns a read-only set of all GUI names.
- **Parameters**: None.
- **Returns**: A `Set<String>` containing GUI names.
- **Example**:
  ```java
  Set<String> guis = api.listGUIs();
  guis.forEach(System.out::println);
  ```

## Building GUI Items with GUIItemBuilder

The `GUIItemBuilder` class simplifies the creation of `GUIItem` objects, which represent items in a GUI with properties like material, name, lore, takeability, and actions.

### Methods
- **material(String material)**: Sets the item material (e.g., `"DIAMOND"`). Defaults to `"AIR"`. Optional.
- **name(String name)**: Sets the display name. Defaults to `null`. Optional.
- **lore(List<String> lore)**: Sets the lore. Defaults to an empty list. Optional.
- **addLore(String line)**: Adds a single lore line. Required if called.
- **takeable(boolean takeable)**: Sets whether the item can be taken. Defaults to `false`. Optional.
- **addAction(GUIAction action)**: Adds a custom action. Defaults to no actions. Optional.
- **addCommandAction(String command, boolean asConsole)**: Adds a command action. `command` is required; `asConsole` defaults to `false`. Partially optional.
- **addMessageAction(String message)**: Adds a message action. `message` is required.
- **addSoundAction(String sound, float volume, float pitch)**: Adds a sound action. `sound` is required; `volume` and `pitch` default to `1.0f`. Partially optional.
- **addCloseAction()**: Adds a GUI close action. No parameters. Optional.
- **actions(List<GUIAction> actions)**: Sets multiple actions. Defaults to an empty list. Optional.
- **build()**: Creates the `GUIItem`. Required to finalize.

### Example
```java
GUIItem item = new GUIItemBuilder()
    .material("DIAMOND")
    .name("§aReward")
    .lore(List.of("§7Click to claim"))
    .takeable(true)
    .addCommandAction("give %player% diamond 1", false)
    .addMessageAction("§aYou claimed a reward!")
    .addSoundAction("ENTITY_EXPERIENCE_ORB_PICKUP", 1.0f, 1.0f)
    .addCloseAction()
    .build();
api.setItem("reward_gui", 1, 10, item);
```

## Handling GUI Clicks with GUIClickEvent

The `GUIClickEvent` is fired when a player clicks a slot in a `CustomGUI` (identified by `GUIHolder`). It provides details about the click and allows cancellation.

### Properties
- **getPlayer()**: The player who clicked.
- **getGui()**: The `CustomGUI` clicked.
- **getPageId()**: The page ID.
- **getSlot()**: The slot index.
- **getItem()**: The `GUIItem` in the slot (may be `null` if empty).
- **isBackButton()**: Returns `true` if the clicked slot is the back button (slot 0), allowing custom handling of the back button.

### Methods
- **isCancelled()**: Checks if the event is cancelled.
- **setCancelled(boolean)**: Cancels the event, preventing `GUIAction` execution. Optional.

### Customizing the Back Button (Slot 0)
When `CoderyoGUI` is used as a dependency, slot 0 (back button) can be customized by listening for `GUIClickEvent` and checking `isBackButton()`. If no custom handler is provided, slot 0 has no default action. When running standalone, `CoderyoGUI` provides a default handler that opens the main menu.

### Example
```java
public class MyPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onGUIClick(GUIClickEvent event) {
        if (event.isBackButton()) {
            Player player = event.getPlayer();
            event.setCancelled(true);
            player.sendMessage("§aYou clicked the back button!");
            player.closeInventory(); // Custom action: close the GUI
        }
    }
}
```

## Complete Example

This example creates a GUI, adds an item with multiple actions, opens it for a player, and handles back button clicks.

```java
public class MyPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void createRewardGUI(Player player) {
        try {
            CoderyoGUIAPI api = CoderyoGUIAPI.getInstance();
            String guiName = "reward_gui_" + player.getUniqueId().toString().substring(0, 8);
            if (guiName.length() > 32) {
                player.sendMessage("§cGUI name too long!");
                return;
            }

            // Create a 3-row GUI
            if (!api.createGUI(guiName, 3)) {
                player.sendMessage("§cFailed to create GUI!");
                return;
            }

            // Add a reward item in slot 10
            GUIItem item = new GUIItemBuilder()
                .material("DIAMOND")
                .name("§aClaim Reward")
                .lore(List.of("§7Click to claim"))
                .takeable(true)
                .addCommandAction("give %player% diamond 1", false)
                .addMessageAction("§aYou claimed a reward!")
                .addSoundAction("ENTITY_EXPERIENCE_ORB_PICKUP", 1.0f, 1.0f)
                .addCloseAction()
                .build();
            api.setItem(guiName, 1, 10, item);

            // Set page to non-interactable
            api.setPageInteractable(guiName, 1, false);

            // Open the GUI
            if (!api.openGUI(player, guiName, 1)) {
                player.sendMessage("§cFailed to open GUI!");
            }
        } catch (IllegalStateException e) {
            player.sendMessage("§cCoderyoGUI not initialized!");
            getLogger().severe("API error: " + e.getMessage());
        }
    }

    @EventHandler
    public void onGUIClick(GUIClickEvent event) {
        Player player = event.getPlayer();
        if (event.isBackButton()) {
            event.setCancelled(true);
            player.sendMessage("§aBack button clicked! Closing GUI.");
            player.closeInventory();
        } else if (event.getSlot() == 10) {
            player.sendMessage("§eYou clicked the reward item!");
        }
    }
}
```

## Notes
- **Error Handling**: Always check return values (`boolean` or `null`) to handle invalid inputs (e.g., non-existent GUI).
- **GUI Name Length**: The GUI name must be 1-32 characters long, non-null, and non-empty. Exceeding this limit will cause `createGUI` to return `false`.
- **Optional Parameters**: `GUIItemBuilder` offers many optional settings (e.g., `name`, `lore`, `takeable`) with defaults, while `CoderyoGUIAPI` methods require all parameters.
- **Data Persistence**: All API modifications are saved to `guis.yml` automatically.
- **Event Cancellation**: Use `GUIClickEvent.setCancelled(true)` to prevent `GUIAction` execution or default back button behavior.
- **Back Button**: Slot 0 is reserved as the back button. Use `GUIClickEvent.isBackButton()` to customize its behavior when used as a dependency. In standalone mode, it opens the main menu by default.
- **Testing**: Ensure `CoderyoGUI` is enabled and test with valid inputs to avoid exceptions.

For further assistance or feature requests, contact the plugin developer.