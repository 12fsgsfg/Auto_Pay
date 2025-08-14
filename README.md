# AutoPay 插件

一個為 PurpurMC 伺服器設計的自動付款插件，可以定期給在線玩家發放金錢，並發送通知和音效。

## 功能特點

- 🕒 **定時付款**: 可配置的付款間隔（預設 30 分鐘）
- 💰 **自動發錢**: 自動給在線玩家發放指定金額
- 🔔 **智能通知**: 可自定義的通知消息和音效
- 🎵 **音效支持**: 支持多種 Minecraft 音效
- ⚙️ **個人設置**: 玩家可以選擇開啟/關閉通知
- 🗄️ **數據持久化**: 使用 YAML 文件保存玩家設置
- 🔧 **管理指令**: 管理員可以重新載入配置

## 安裝要求

- **伺服器**: PurpurMC 1.20.4+
- **Java**: Java 17+
- **依賴插件**: Vault + 經濟插件（如 EssentialsX、iConomy 等）

## 安裝步驟

1. 下載 `AutoPay.jar` 文件
2. 將文件放入伺服器的 `plugins` 資料夾
3. 重啟伺服器或重載插件
4. 編輯 `plugins/AutoPay/config.yml` 配置文件
5. 重載插件配置

## 配置文件

### config.yml

```yaml
# 自動付款設置
auto-pay:
  enabled: true                    # 是否啟用自動付款
  interval: 30                     # 付款間隔（分鐘）
  amount: 100.0                    # 每次付款金額
  
  # 通知設置
  notifications:
    enabled: true                  # 是否啟用通知
    message: "&a[自動付款] &f您收到了 &e{amount} &f金錢！"
    
    # 音效設置
    sound:
      enabled: true                # 是否啟用音效
      type: "ENTITY_PLAYER_LEVELUP" # 音效類型
      volume: 1.0                  # 音量 (0.0-1.0)
      pitch: 1.0                   # 音調 (0.0-2.0)
```

## 指令使用

### 玩家指令

- `/autopay on` - 啟用自動付款通知
- `/autopay off` - 停用自動付款通知
- `/autopay status` - 查看通知狀態

### 管理員指令

- `/autopay reload` - 重新載入插件配置（需要 `autopay.admin` 權限）

## 權限節點

- `autopay.use` - 允許使用基本指令（預設：所有玩家）
- `autopay.admin` - 允許使用管理指令（預設：OP）

## 音效類型

插件支持所有 Minecraft 音效，常用音效包括：

- `ENTITY_PLAYER_LEVELUP` - 升級音效
- `BLOCK_NOTE_BLOCK_PLING` - 音符盒音效
- `ENTITY_EXPERIENCE_ORB_PICKUP` - 經驗球音效
- `ENTITY_PLAYER_LEVELUP` - 玩家升級音效

## 變數支持

通知消息支持以下變數：

- `{amount}` - 付款金額
- `{player}` - 玩家名稱

## 故障排除

### 常見問題

1. **插件無法啟動**
   - 檢查是否安裝了 Vault 和經濟插件
   - 確認 Java 版本是否為 17+

2. **經濟插件不工作**
   - 確認 Vault 版本與經濟插件兼容
   - 檢查經濟插件是否正確配置

3. **音效不播放**
   - 檢查音效名稱是否正確
   - 確認音量和音調設置

### 日誌檢查

插件會在控制台輸出詳細日誌，包括：
- 啟動和停止信息
- 付款執行記錄
- 錯誤和警告信息

## 開發信息

- **版本**: 1.0.0
- **作者**: YourName
- **支持**: PurpurMC 1.20.4+
- **許可證**: MIT

## 更新日誌

### v1.0.0
- 初始版本發布
- 基本自動付款功能
- 通知和音效支持
- 玩家個人設置
- 管理員指令支持

## 支持與反饋

如果您遇到問題或有建議，請：
1. 檢查此 README 的故障排除部分
2. 查看伺服器控制台的錯誤日誌
3. 聯繫插件開發者

---

**注意**: 此插件需要 Vault 和經濟插件才能正常工作。請確保您的伺服器已正確安裝這些依賴插件。
