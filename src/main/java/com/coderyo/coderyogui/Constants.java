package com.coderyo.coderyogui;

import org.bukkit.Material;

/**
 * 常數類別，用於存放專案中所有魔術數字和常數值
 * Constants class for storing all magic numbers and constant values in the project
 */
public final class Constants {

    // 物品欄大小 / Inventory sizes
    /** 標準雙箱物品欄大小 (6行 x 9列) - 用於主選單、編輯器等大型介面 */
    public static final int INVENTORY_SIZE_54 = 54;
    /** 單箱物品欄大小 (4行 x 9列) - 用於行數選擇等小型介面 */
    public static final int INVENTORY_SIZE_36 = 36;
    /** 小物品欄大小 (1行 x 9列) - 用於上下文選單等 */
    public static final int INVENTORY_SIZE_9 = 9;

    // 每頁項目數 / Items per page
    /** 每頁顯示的最大項目數量 - 確保分頁功能的一致性 */
    public static final int ITEMS_PER_PAGE = 45;

    // 主菜單按鈕位置 / Main menu button positions
    /** 主菜單創建GUI按鈕位置 - 位於物品欄左上角 */
    public static final int MAIN_MENU_CREATE_BUTTON = 0;
    /** 主菜單打開GUI按鈕位置 - 用於查看現有GUI */
    public static final int MAIN_MENU_OPEN_BUTTON = 1;
    /** 主菜單編輯GUI按鈕位置 - 用於編輯現有GUI */
    public static final int MAIN_MENU_EDIT_BUTTON = 2;
    /** 主菜單刪除GUI按鈕位置 - 用於刪除現有GUI */
    public static final int MAIN_MENU_DELETE_BUTTON = 3;

    // 分頁按鈕位置 / Pagination button positions
    /** 上一頁按鈕位置 - 位於物品欄右下角 */
    public static final int PREVIOUS_PAGE_BUTTON = 52;
    /** 下一頁按鈕位置 - 位於物品欄右下角最後一個位置 */
    public static final int NEXT_PAGE_BUTTON = 53;

    // 編輯器按鈕位置 / Editor button positions
    /** 編輯器返回按鈕位置 - 返回主選單 */
    public static final int EDITOR_BACK_BUTTON = 0;
    /** 編輯器設置名稱按鈕位置 - 設定GUI顯示名稱 */
    public static final int EDITOR_SET_NAME_BUTTON = 1;
    /** 編輯器設置行數按鈕位置 - 設定GUI行數 */
    public static final int EDITOR_SET_ROWS_BUTTON = 2;
    /** 編輯器添加頁面按鈕位置 - 新增GUI頁面 */
    public static final int EDITOR_ADD_PAGE_BUTTON = 3;
    /** 編輯器刪除頁面按鈕位置 - 刪除當前頁面 */
    public static final int EDITOR_DELETE_PAGE_BUTTON = 4;
    /** 編輯器上一頁按鈕位置 - 切換到上一頁 */
    public static final int EDITOR_PREVIOUS_PAGE_BUTTON = 5;
    /** 編輯器下一頁按鈕位置 - 切換到下一頁 */
    public static final int EDITOR_NEXT_PAGE_BUTTON = 6;
    /** 編輯器切換交互按鈕位置 - 開關物品可否被拿取 */
    public static final int EDITOR_TOGGLE_INTERACT_BUTTON = 7;
    /** 編輯器預覽按鈕位置 - 預覽GUI最終效果 */
    public static final int EDITOR_PREVIEW_BUTTON = 8;

    // 內容區域起始位置 / Content area start position
    /** 內容區域起始槽位位置 - 從第9個槽位開始顯示實際內容 */
    public static final int CONTENT_AREA_START = 9;

    // 上下文菜單按鈕位置 / Context menu button positions
    /** 物品設置選單中的選擇物品按鈕位置 */
    public static final int CONTEXT_SELECT_ITEM_BUTTON = 0;
    /** 物品設置選單中的設置玩家命令按鈕位置 */
    public static final int CONTEXT_SET_PLAYER_COMMAND_BUTTON = 1;
    /** 物品設置選單中的設置控制台命令按鈕位置 */
    public static final int CONTEXT_SET_CONSOLE_COMMAND_BUTTON = 2;
    /** 物品設置選單中的設置物品名稱按鈕位置 */
    public static final int CONTEXT_SET_NAME_BUTTON = 3;
    /** 物品設置選單中的刪除物品按鈕位置 */
    public static final int CONTEXT_DELETE_ITEM_BUTTON = 4;
    /** 物品設置選單中的管理命令按鈕位置 */
    public static final int CONTEXT_MANAGE_COMMANDS_BUTTON = 5;
    /** 物品設置選單中的返回按鈕位置 */
    public static final int CONTEXT_BACK_BUTTON = 8;

    // 物品選擇按鈕位置 / Item select button positions
    /** 物品選擇介面中的返回按鈕位置 */
    public static final int ITEM_SELECT_BACK_BUTTON = 0;
    /** 物品選擇介面中的搜尋按鈕位置 */
    public static final int ITEM_SELECT_SEARCH_BUTTON = 1;

    // 命令管理按鈕位置 / Command manager button positions
    /** 命令管理介面中的返回按鈕位置 */
    public static final int COMMAND_MANAGER_BACK_BUTTON = 0;
    /** 命令管理介面中的添加新命令按鈕位置 */
    public static final int COMMAND_MANAGER_ADD_BUTTON = 1;

    // 刪除確認按鈕位置 / Delete confirm button positions
    /** 刪除確認介面中的取消按鈕位置 */
    public static final int DELETE_CONFIRM_CANCEL_BUTTON = 5;
    /** 刪除確認介面中的確認按鈕位置 */
    public static final int DELETE_CONFIRM_CONFIRM_BUTTON = 3;

    // GUI 配置 / GUI configuration
    /** GUI最大行數限制 - Minecraft物品欄最大為6行 */
    public static final int MAX_GUI_ROWS = 6;
    /** GUI最小行數限制 - 最少為1行 */
    public static final int MIN_GUI_ROWS = 1;
    /** GUI預設頁面編號 - 新建GUI的起始頁面 */
    public static final int DEFAULT_GUI_PAGE = 1;

    // 命令和訊息長度限制 / Command and message length limits
    /** 命令的最大長度限制 - 防止過長命令造成問題 */
    public static final int MAX_COMMAND_LENGTH = 100;
    /** 聊天訊息的最大長度限制 - 符合Minecraft限制 */
    public static final int MAX_MESSAGE_LENGTH = 100;
    /** 音效名稱的最大長度限制 - 防止無效音效名稱 */
    public static final int MAX_SOUND_NAME_LENGTH = 100;

    // 移動距離限制 / Movement distance limits
    /** 告示牌輸入的最大交互距離 - 防止玩家遠距離操作 */
    public static final double MAX_SIGN_INTERACTION_DISTANCE = 5.0;

    // 任務定時器參數 / Task timer parameters
    /** 告示牌清理任務的延遲時間 - 立即開始 */
    public static final long SIGN_CLEANUP_TASK_DELAY = 0L;
    /** 告示牌清理任務的執行週期 - 每秒檢查一次 */
    public static final long SIGN_CLEANUP_TASK_PERIOD = 20L;

    // 位置偏移 / Location offsets
    /** 告示牌向下偏移距離 - 用於尋找合適位置 */
    public static final double SIGN_LOCATION_Y_OFFSET_DOWN = -2.0;
    /** 告示牌向上偏移距離 - 用於尋找合適位置 */
    public static final double SIGN_LOCATION_Y_OFFSET_UP = 2.0;

    // Holder 預設值 / Holder default values
    /** 槽位位置的預設值 - 表示無效或未設定 */
    public static final int DEFAULT_SLOT_VALUE = -1;

    // 附魔等級 / Enchantment levels
    /** 預設附魔等級 - 用於UI視覺效果 */
    public static final int DEFAULT_ENCHANTMENT_LEVEL = 1;

    // 音效參數 / Sound parameters
    /** 預設音效音量 - 正常音量 */
    public static final float DEFAULT_SOUND_VOLUME = 1.0f;
    /** 預設音效音調 - 正常音調 */
    public static final float DEFAULT_SOUND_PITCH = 1.0f;

    // 字串常數 / String constants
    /** 預設材質名稱 - 用於空物品槽位 */
    public static final String DEFAULT_MATERIAL = "AIR";

    // Material常數 / Material constants
    /** 預設材質類型 - 用於空物品槽位 */
    public static final Material DEFAULT_MATERIAL_TYPE = Material.AIR;
    /** 關閉GUI動作的值 */
    public static final String CLOSE_ACTION_VALUE = "close";
    /** 空字串常數 - 用於初始化和比較 */
    public static final String EMPTY_STRING = "";

    // 權限常數 / Permission constants
    /** 創建GUI的權限 - 允許玩家創建新GUI */
    public static final String PERMISSION_CREATE = "coderyogui.create";
    /** 使用GUI的權限 - 允許玩家開啟和使用GUI */
    public static final String PERMISSION_USE = "coderyogui.use";
    /** 編輯所有GUI的權限 - 允許玩家編輯任何GUI */
    public static final String PERMISSION_EDIT_ALL = "coderyogui.edit.*";
    /** 刪除GUI的權限 - 允許玩家刪除GUI */
    public static final String PERMISSION_DELETE = "coderyogui.del";

    // 狀態常數 / State constants
    /** 創建GUI的狀態 - 用於處理GUI創建流程 */
    public static final String STATE_CREATE_GUI = "create_gui";
    /** 設置名稱的狀態 - 用於處理GUI名稱設置 */
    public static final String STATE_SET_NAME = "set_name";
    /** 設置命令的狀態 - 用於處理命令設置 */
    public static final String STATE_SET_COMMAND = "set_command";
    /** 設置物品名稱的狀態 - 用於處理物品顯示名稱設置 */
    public static final String STATE_SET_ITEM_NAME = "set_item_name";
    /** 刪除物品的狀態 - 用於處理物品刪除 */
    public static final String STATE_DELETE_ITEM = "delete_item";
    /** 編輯命令的狀態 - 用於處理命令編輯 */
    public static final String STATE_EDIT_COMMAND = "edit_command";
    /** 刪除命令的狀態 - 用於處理命令刪除 */
    public static final String STATE_DELETE_COMMAND = "delete_command";
    /** 搜尋物品的狀態 - 用於處理物品搜尋 */
    public static final String STATE_SEARCH_ITEM = "search_item";
    /** 搜尋GUI的狀態 - 用於處理GUI搜尋 */
    public static final String STATE_SEARCH_GUI = "search_gui";

    // 臨時資料標記 / Temporary data flags
    /** 控制台命令標記 - 表示命令應在控制台執行 */
    public static final String TEMP_FLAG_CONSOLE = "console";
    /** 新增標記 - 表示新增操作 */
    public static final String TEMP_FLAG_NEW = "new";
    /** 編輯標記 - 表示編輯操作 */
    public static final String TEMP_FLAG_EDIT = "edit";
    /** 刪除標記 - 表示刪除操作 */
    public static final String TEMP_FLAG_DELETE = "delete";
    /** 打開標記 - 表示打開操作 */
    public static final String TEMP_FLAG_OPEN = "open";

    // 介面標題 / Interface titles
    /** 主選單介面標題 */
    public static final String TITLE_MAIN_MENU = "CoderyoGUI 主菜單";
    /** 物品設置介面標題 */
    public static final String TITLE_ITEM_SETTINGS = "物品設置";
    /** 物品選擇介面標題 */
    public static final String TITLE_SELECT_ITEM = "選擇物品";
    /** 物品搜尋介面標題前綴 */
    public static final String TITLE_SEARCH_ITEM_PREFIX = "搜尋物品: ";
    /** 行數選擇介面標題 */
    public static final String TITLE_SELECT_ROWS = "選擇行數";
    /** 命令管理介面標題前綴 */
    public static final String TITLE_MANAGE_COMMANDS_PREFIX = "管理命令: 槽位 ";

    // 物品顯示名稱 / Item display names
    /** 返回按鈕顯示名稱 */
    public static final String DISPLAY_BACK = "§c返回";
    /** 上一頁按鈕顯示名稱 */
    public static final String DISPLAY_PREVIOUS_PAGE = "§a上一頁";
    /** 下一頁按鈕顯示名稱 */
    public static final String DISPLAY_NEXT_PAGE = "§a下一頁";
    /** 上一頁按鈕禁用時的顯示名稱 */
    public static final String DISPLAY_PREVIOUS_PAGE_DISABLED = "§7上一頁 (無)";
    /** 下一頁按鈕禁用時的顯示名稱 */
    public static final String DISPLAY_NEXT_PAGE_DISABLED = "§7下一頁 (無)";
    /** 創建GUI按鈕顯示名稱 */
    public static final String DISPLAY_CREATE_GUI = "§a創建新 GUI";
    /** 打開GUI按鈕顯示名稱 */
    public static final String DISPLAY_OPEN_GUI = "§a打開現有 GUI";
    /** 編輯GUI按鈕顯示名稱 */
    public static final String DISPLAY_EDIT_GUI = "§a編輯 GUI";
    /** 刪除GUI按鈕顯示名稱 */
    public static final String DISPLAY_DELETE_GUI = "§c刪除 GUI";
    /** 返回主選單按鈕顯示名稱 */
    public static final String DISPLAY_RETURN_MAIN_MENU = "§c返回主菜單";
    /** 設置名稱按鈕顯示名稱 */
    public static final String DISPLAY_SET_NAME = "§a設置名稱";
    /** 選擇物品按鈕顯示名稱 */
    public static final String DISPLAY_SELECT_ITEM = "§a選擇物品";
    /** 設置玩家命令按鈕顯示名稱 */
    public static final String DISPLAY_SET_PLAYER_COMMAND = "§a設置玩家命令";
    /** 設置控制台命令按鈕顯示名稱 */
    public static final String DISPLAY_SET_CONSOLE_COMMAND = "§a設置控制台命令";
    /** 設置物品名稱按鈕顯示名稱 */
    public static final String DISPLAY_SET_ITEM_NAME = "§a設置名稱";
    /** 刪除物品按鈕顯示名稱 */
    public static final String DISPLAY_DELETE_ITEM = "§c刪除物品";
    /** 管理命令按鈕顯示名稱 */
    public static final String DISPLAY_MANAGE_COMMANDS = "§a管理命令";
    /** 搜尋物品按鈕顯示名稱 */
    public static final String DISPLAY_SEARCH_ITEM = "§a搜尋物品";
    /** 添加新命令按鈕顯示名稱 */
    public static final String DISPLAY_ADD_NEW_COMMAND = "§a添加新命令";
    /** 無綁定命令時的顯示名稱 */
    public static final String DISPLAY_NO_COMMANDS = "§c無綁定命令";
    /** 無匹配物品時的顯示名稱 */
    public static final String DISPLAY_NO_MATCHING_ITEMS = "§c無匹配物品";

    // 物品描述 / Item lore
    /** 創建按鈕的描述文字 */
    public static final String LORE_CLICK_CREATE = "§7點擊創建";
    /** 打開按鈕的描述文字 */
    public static final String LORE_CLICK_OPEN = "§7點擊查看";
    /** 編輯按鈕的描述文字 */
    public static final String LORE_CLICK_EDIT = "§7點擊編輯";
    /** 刪除按鈕的描述文字 */
    public static final String LORE_CLICK_DELETE = "§7點擊刪除";
    /** 返回按鈕的描述文字 */
    public static final String LORE_CLICK_RETURN = "§7點擊返回";
    /** 選擇按鈕的描述文字 */
    public static final String LORE_CLICK_SELECT = "§7點擊選擇";
    /** 設置名稱按鈕的描述文字 */
    public static final String LORE_CLICK_SET_NAME = "§7輸入新名稱";
    /** 選擇行數按鈕的描述文字 */
    public static final String LORE_CLICK_SELECT_ROWS = "§7點擊選擇行數";
    /** 添加頁面按鈕的描述文字 */
    public static final String LORE_CLICK_ADD_PAGE = "§7點擊添加新頁面";
    /** 刪除頁面按鈕的描述文字 */
    public static final String LORE_CLICK_DELETE_PAGE = "§7點擊刪除當前頁面";
    /** 切換頁面按鈕的描述文字 */
    public static final String LORE_CLICK_SWITCH_PAGE = "§7點擊切換";
    /** 切換交互狀態按鈕的描述文字 */
    public static final String LORE_CLICK_TOGGLE_INTERACT = "§7點擊切換交互狀態";
    /** 預覽按鈕的描述文字 */
    public static final String LORE_CLICK_PREVIEW = "§7查看最終效果";
    /** 設置物品按鈕的描述文字 */
    public static final String LORE_CLICK_SET_ITEM = "§7設置槽位物品";
    /** 添加命令按鈕的描述文字 */
    public static final String LORE_CLICK_ADD_COMMAND = "§7添加執行命令";
    /** 添加控制台命令按鈕的描述文字 */
    public static final String LORE_CLICK_CONSOLE_COMMAND = "§7添加控制台命令";
    /** 自訂物品名稱按鈕的描述文字 */
    public static final String LORE_CLICK_CUSTOM_NAME = "§7自訂物品名稱";
    /** 移除物品按鈕的描述文字 */
    public static final String LORE_CLICK_REMOVE_ITEM = "§7移除此槽位物品";
    /** 編輯命令按鈕的描述文字 */
    public static final String LORE_CLICK_EDIT_COMMANDS = "§7編輯或刪除綁定命令";
    /** 搜尋按鈕的描述文字 */
    public static final String LORE_CLICK_SEARCH = "§7輸入關鍵詞（如 diamond）";
    /** 添加新命令按鈕的描述文字 */
    public static final String LORE_CLICK_ADD_NEW_COMMAND = "§7點擊添加命令";
    /** 編輯或刪除按鈕的描述文字 */
    public static final String LORE_CLICK_EDIT_OR_DELETE = "§7左鍵編輯，右鍵刪除";
    /** 嘗試其他關鍵詞的提示文字 */
    public static final String LORE_TRY_OTHER_KEYWORD = "§7請嘗試其他關鍵詞";
    /** 切換按鈕的描述文字 */
    public static final String LORE_CLICK_SWITCH = "§7點擊切換";
    /** 取消按鈕的描述文字 */
    public static final String LORE_CLICK_CANCEL = "§7點擊取消";
    /** 無綁定命令時的提示文字 */
    public static final String LORE_NO_BOUND_COMMANDS = "§7請添加新命令";

    // 訊息常數 / Message constants
    /** 返回主選單成功訊息 */
    public static final String MSG_RETURNED_TO_MAIN_MENU = "§aReturned to main menu";
    /** 無權限創建GUI的錯誤訊息 */
    public static final String MSG_NO_PERMISSION_CREATE = "§c無權限創建 GUI！";
    /** 無權限打開GUI的錯誤訊息 */
    public static final String MSG_NO_PERMISSION_OPEN = "§c無權限打開 GUI！";
    /** 無權限編輯GUI的錯誤訊息 */
    public static final String MSG_NO_PERMISSION_EDIT = "§c無權限編輯 GUI！";
    /** 無權限刪除GUI的錯誤訊息 */
    public static final String MSG_NO_PERMISSION_DELETE = "§c無權限刪除 GUI！";
    /** GUI打開成功訊息前綴 */
    public static final String MSG_GUI_OPENED = "§a已打開 GUI: ";
    /** GUI不存在的錯誤訊息 */
    public static final String MSG_GUI_NOT_FOUND = "§cGUI 不存在！";
    /** 開始編輯GUI的成功訊息前綴 */
    public static final String MSG_GUI_EDITING = "§a正在編輯 GUI: ";
    /** 返回主選單的成功訊息（中文） */
    public static final String MSG_RETURNED_TO_MAIN_MENU_ALT = "§a已返回主菜單";
    /** GUI刪除成功的訊息前綴 */
    public static final String MSG_GUI_DELETED = "§a已刪除 GUI: ";
    /** 切換到上一頁的成功訊息 */
    public static final String MSG_SWITCHED_TO_PREVIOUS_PAGE = "§a已切換到上一頁";
    /** 切換到下一頁的成功訊息 */
    public static final String MSG_SWITCHED_TO_NEXT_PAGE = "§a已切換到下一頁";
    /** 已在第一頁的提示訊息 */
    public static final String MSG_FIRST_PAGE = "§7已是第一頁";
    /** GUI不存在的錯誤訊息（重複） */
    public static final String MSG_GUI_DOES_NOT_EXIST = "§cGUI 不存在！";
    /** 無效GUI或頁面的錯誤訊息 */
    public static final String MSG_INVALID_GUI_OR_PAGE = "§c無效 GUI 或頁面！";
    /** 取消刪除的成功訊息 */
    public static final String MSG_CANCELLED_DELETION = "§a已取消刪除";
    /** 無效GUI的錯誤訊息 */
    public static final String MSG_INVALID_GUI = "§c無效 GUI！";
    /** 需要先設置物品的提示訊息 */
    public static final String MSG_SET_ITEM_FIRST = "§c請先為該槽位設置一個物品！";
    /** 無匹配物品的錯誤訊息 */
    public static final String MSG_NO_MATCHING_ITEMS = "§c無匹配物品";
    /** 嘗試不同關鍵詞的提示訊息 */
    public static final String MSG_TRY_DIFFERENT_KEYWORD = "§7請嘗試其他關鍵詞";
    /** 切換到上一頁的成功訊息（重複） */
    public static final String MSG_SWITCHED_TO_PREVIOUS_PAGE_ALT = "§a已切換到上一頁";
    /** 切換到下一頁的成功訊息（重複） */
    public static final String MSG_SWITCHED_TO_NEXT_PAGE_ALT = "§a已切換到下一頁";
    /** 已在第一頁的提示訊息（重複） */
    public static final String MSG_ALREADY_FIRST_PAGE = "§7已是第一頁";
    /** 無綁定命令的提示訊息 */
    public static final String MSG_NO_BOUND_COMMANDS = "§7請添加新命令";
    /** 輸入因移動過遠而取消的訊息 */
    public static final String MSG_INPUT_CANCELLED_MOVED_TOO_FAR = "§c輸入取消：移動過遠";
    /** 輸入會話已過期的錯誤訊息 */
    public static final String MSG_SESSION_EXPIRED = "§c輸入會話已過期，請重新操作！";
    /** 無效輸入的錯誤訊息 */
    public static final String MSG_INVALID_INPUT = "§c無效輸入，請重新輸入";
    /** 無法找到更新GUI的錯誤訊息 */
    public static final String MSG_CANNOT_FIND_GUI = "§c無法找到更新的 GUI，請重新編輯！";

    // 日誌訊息 / Log messages
    /** 主選單點擊的日誌訊息格式 */
    public static final String LOG_CLICKED_MAIN_MENU = "玩家 %s 點擊主菜單，槽位 %d";
    /** GUI選擇列表點擊的日誌訊息格式 */
    public static final String LOG_CLICKED_GUI_LIST = "玩家 %s 點擊 GUI 選擇列表，槽位 %d，模式: %s";
    /** 刪除GUI列表點擊的日誌訊息格式 */
    public static final String LOG_CLICKED_DELETE_LIST = "玩家 %s 點擊刪除 GUI 列表，槽位 %d";
    /** 刪除確認界面點擊的日誌訊息格式 */
    public static final String LOG_CLICKED_DELETE_CONFIRM = "玩家 %s 點擊刪除確認界面，槽位 %d";
    /** 物品選擇列表點擊的日誌訊息格式 */
    public static final String LOG_CLICKED_ITEM_SELECT = "玩家 %s 點擊物品選擇列表，槽位 %d，搜尋: %s，頁數: %d";
    /** 告示牌輸入的日誌訊息格式 */
    public static final String LOG_SIGN_INPUT = "玩家 %s 輸入告示牌內容: %s，狀態: %s";
    /** GUI缺少頁面的警告日誌訊息格式 */
    public static final String LOG_GUI_MISSING_PAGE = "GUI %s 缺少 pageId=%d，已自動創建";
    /** 新建GUI缺少頁面的警告日誌訊息格式 */
    public static final String LOG_NEW_GUI_MISSING_PAGE = "新創建的 GUI %s 缺少 pageId=1，已自動創建";
    /** 物品選擇的詳細日誌訊息格式 */
    public static final String LOG_ITEM_SELECTION = "物品選擇: 槽位 %d，索引 %d，材質數量 %d";

    // 輸入提示 / Input prompts
    /** 輸入新GUI名稱的提示訊息 */
    public static final String PROMPT_ENTER_GUI_NAME = "輸入新 GUI 名稱";
    /** 輸入GUI名稱關鍵詞的提示訊息 */
    public static final String PROMPT_ENTER_GUI_NAME_KEYWORD = "輸入 GUI 名稱關鍵詞";
    /** 輸入GUI名稱的替代提示訊息 */
    public static final String PROMPT_ENTER_GUI_NAME_ALT = "輸入 GUI 名稱";
    /** 輸入玩家命令的提示訊息 */
    public static final String PROMPT_ENTER_PLAYER_COMMAND = "輸入玩家命令（不含 /）";
    /** 輸入控制台命令的提示訊息 */
    public static final String PROMPT_ENTER_CONSOLE_COMMAND = "輸入控制台命令（不含 /）";
    /** 輸入物品名稱的提示訊息 */
    public static final String PROMPT_ENTER_ITEM_NAME = "輸入物品名稱";
    /** 輸入物品名稱關鍵詞的提示訊息 */
    public static final String PROMPT_ENTER_ITEM_KEYWORD = "輸入物品名稱（如 diamond）";
    /** 輸入新命令的提示訊息 */
    public static final String PROMPT_ENTER_NEW_COMMAND = "輸入新命令（不含 /）";
    /** 編輯命令的提示訊息 */
    public static final String PROMPT_EDIT_COMMAND = "編輯命令（不含 /）";
    /** 重新輸入的提示訊息 */
    public static final String PROMPT_REENTER = "重新輸入";

    private Constants() {
        // 私有構造函數防止實例化 / Private constructor to prevent instantiation
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
