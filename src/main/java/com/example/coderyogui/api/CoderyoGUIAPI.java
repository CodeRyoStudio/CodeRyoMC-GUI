package com.coderyo.coderyogui.api;

import com.coderyo.coderyogui.CoderyoGUI;
import com.coderyo.coderyogui.CustomGUI;
import com.coderyo.coderyogui.GUIManager;
import com.coderyo.coderyogui.GUIItem;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CoderyoGUIAPI {
    private static CoderyoGUIAPI instance;
    private final CoderyoGUI plugin;

    private CoderyoGUIAPI(CoderyoGUI plugin) {
        this.plugin = plugin;
    }

    public static void init(CoderyoGUI plugin) {
        instance = new CoderyoGUIAPI(plugin);
    }

    public static CoderyoGUIAPI getCustomInstance() {
        if (instance == null) {
            throw new IllegalStateException("CoderyoGUIAPI 未初始化");
        }
        return instance;
    }

    /**
     * 創建一個新的 GUI
     * @param name GUI 名稱
     * @param rows GUI 行數（1-6）
     * @return 是否創建成功
     */
    public boolean createGUI(String name, int rows) {
        if (name == null || name.isEmpty() || name.length() > 32) {
            return false;
        }
        if (rows < 1 || rows > 6) {
            return false;
        }
        GUIManager guiManager = plugin.getGuiManager();
        if (guiManager.getGUIs().containsKey(name)) {
            return false;
        }
        guiManager.getGUIs().put(name, new CustomGUI(name, rows));
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    /**
     * 設置 GUI 的行數
     * @param name GUI 名稱
     * @param rows 新行數（1-6）
     * @return 是否設置成功
     */
    public boolean setGUIRows(String name, int rows) {
        if (rows < 1 || rows > 6) {
            return false;
        }
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null) {
            return false;
        }
        guiManager.getGUIs().put(name, new CustomGUI(name, rows, gui.pages()));
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    /**
     * 添加新頁面
     * @param name GUI 名稱
     * @param pageId 頁面 ID
     * @return 是否添加成功
     */
    public boolean addPage(String name, int pageId) {
        if (pageId < 1) {
            return false;
        }
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null || gui.pages().containsKey(pageId)) {
            return false;
        }
        gui.pages().put(pageId, new com.coderyo.coderyogui.GUIPage());
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    /**
     * 移除頁面
     * @param name GUI 名稱
     * @param pageId 頁面 ID
     * @return 是否移除成功
     */
    public boolean removePage(String name, int pageId) {
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null || !gui.pages().containsKey(pageId) || gui.pages().size() <= 1) {
            return false;
        }
        gui.pages().remove(pageId);
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    /**
     * 在指定槽位設置物品
     * @param name GUI 名稱
     * @param pageId 頁面 ID
     * @param slot 槽位（9 起）
     * @param item GUI 物品
     * @return 是否設置成功
     */
    public boolean setItem(String name, int pageId, int slot, GUIItem item) {
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null || !gui.pages().containsKey(pageId)) {
            return false;
        }
        if (slot < 9 || slot >= gui.rows() * 9) {
            return false;
        }
        if (item == null) {
            return false;
        }
        gui.pages().get(pageId).items().put(slot, item);
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    /**
     * 移除指定槽位的物品
     * @param name GUI 名稱
     * @param pageId 頁面 ID
     * @param slot 槽位（9 起）
     * @return 是否移除成功
     */
    public boolean removeItem(String name, int pageId, int slot) {
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null || !gui.pages().containsKey(pageId)) {
            return false;
        }
        if (slot < 9 || slot >= gui.rows() * 9) {
            return false;
        }
        gui.pages().get(pageId).items().remove(slot);
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    /**
     * 設置頁面是否可交互
     * @param name GUI 名稱
     * @param pageId 頁面 ID
     * @param allowInteract 是否允許交互
     * @return 是否設置成功
     */
    public boolean setPageInteractable(String name, int pageId, boolean allowInteract) {
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null || !gui.pages().containsKey(pageId)) {
            return false;
        }
        com.coderyo.coderyogui.GUIPage page = gui.pages().get(pageId);
        gui.pages().put(pageId, new com.coderyo.coderyogui.GUIPage(page.items(), allowInteract));
        plugin.getDataStorage().saveGUIsAsync();
        return true;
    }

    /**
     * 刪除 GUI
     * @param name GUI 名稱
     * @return 是否刪除成功
     */
    public boolean deleteGUI(String name) {
        GUIManager guiManager = plugin.getGuiManager();
        if (guiManager.getGUIs().remove(name) != null) {
            plugin.getDataStorage().saveGUIsAsync();
            return true;
        }
        return false;
    }

    /**
     * 為玩家打開 GUI
     * @param player 玩家
     * @param name GUI 名稱
     * @param pageId 頁面 ID
     * @return 是否打開成功
     */
    public boolean openGUI(Player player, String name, int pageId) {
        if (player == null) {
            return false;
        }
        GUIManager guiManager = plugin.getGuiManager();
        CustomGUI gui = guiManager.getGUI(name);
        if (gui == null || !gui.pages().containsKey(pageId)) {
            return false;
        }
        player.openInventory(gui.getPage(pageId));
        return true;
    }

    /**
     * 獲取 GUI 數據（只讀副本）
     * @param name GUI 名稱
     * @return GUI 對象或 null
     */
    public CustomGUI getGUI(String name) {
        CustomGUI gui = plugin.getGuiManager().getGUI(name);
        if (gui == null) {
            return null;
        }
        // 返回只讀副本
        return new CustomGUI(gui.name(), gui.rows(), new HashMap<>(gui.pages()));
    }

    /**
     * 列出所有 GUI 名稱
     * @return GUI 名稱集合
     */
    public Set<String> listGUIs() {
        return Collections.unmodifiableSet(plugin.getGuiManager().getGUIs().keySet());
    }
}