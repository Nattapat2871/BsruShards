

# 💎 BrsuShards Plugin

<div align="center">

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![GitHub Repo stars](https://img.shields.io/github/stars/Nattapat2871/BsruShards?style=flat-square)](https://github.com/Nattapat2871/BsruShards/stargazers)
![Visitor Badge](https://api.visitorbadge.io/api/VisitorHit?user=Nattapat2871&repo=BsruShards&countColor=%237B1E7A&style=flat-square)

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/Nattapat2871)
</div>

<p align= "center">
        <a href="/README.md">English</a>   <b>ภาษาไทย</b>　

ปลั๊กอินสำหรับจัดการระบบแต้ม Shards ที่ออกแบบมาเพื่อใช้งานบนเซิร์ฟเวอร์ BSRU Minecraft โดยเฉพาะ มาพร้อมกับระบบแจกแต้มอัตโนมัติ, ร้านค้า GUI, และรองรับ PlaceholderAPI

---

## ✨ คุณสมบัติหลัก (Features)

* **🪙 แจกแต้มอัตโนมัติ:** แจกแต้ม Shards ให้กับผู้เล่นที่ออนไลน์อยู่เป็นประจำตามเวลาที่กำหนดใน `config.yml`
* **🏝️ โซน AFK รับแต้ม:** สร้างพื้นที่พิเศษที่ผู้เล่นสามารถเข้าไปยืนรอเพื่อรับ Shards ได้อย่างต่อเนื่อง
* **🛒 ร้านค้า GUI:** ผู้เล่นสามารถใช้คำสั่ง `/shardsshop` เพื่อเปิดร้านค้าสำหรับแลกไอเทมด้วยแต้ม Shards
* **✅ หน้าต่างยืนยันการซื้อ:** มี UI ยืนยันก่อนการซื้อไอเทมเพื่อป้องกันการกดพลาด
* **👨‍💻 คำสั่งแอดมิน:** จัดการผู้เล่น, ร้านค้า, และโซน AFK ได้ง่ายๆ ผ่านคำสั่ง `/bsrushards`
* **📊 รองรับ PlaceholderAPI:** สามารถดึงข้อมูลแต้มไปแสดงผลบนปลั๊กอินอื่นๆ ได้ เช่น Scoreboard, Tab
* **🔧 ตั้งค่าง่าย:** ปรับแต่งข้อความ, เสียง, และหน้าตา UI ได้เกือบทั้งหมดผ่านไฟล์ `config.yml`

---

## ⚙️ สิ่งที่ต้องมี (Dependencies)

* **Server:** Spigot หรือ Paper (แนะนำเวอร์ชัน 1.16 ขึ้นไป)
* **Optional:** [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) - เพื่อใช้งานระบบ `%placeholder%`

---

## 🚀 การติดตั้ง (Installation)

1.  ดาวน์โหลดไฟล์ `.jar` ของปลั๊กอิน BrsuShards
2.  นำไฟล์ที่ดาวน์โหลดไปวางในโฟลเดอร์ `plugins` ของเซิร์ฟเวอร์
3.  รีสตาร์ทเซิร์ฟเวอร์เพื่อให้ปลั๊กอินสร้างไฟล์ตั้งค่าต่างๆ
4.  (แนะนำ) ติดตั้ง PlaceholderAPI เพื่อใช้งานได้เต็มรูปแบบ
5.  เข้าไปตั้งค่าต่างๆ ได้ที่ไฟล์ `plugins/BrsuShards/config.yml`

---

## 📝 คำสั่งและ Permissions

| คำสั่ง | Permission | คำอธิบาย |
| :--- | :--- | :--- |
| `/shards` | `bsrushards.user` | ตรวจสอบจำนวน Shards ของตัวเอง |
| `/shardsshop` | `bsrushards.user` | เปิดร้านค้า Shards |
| `/bsrushards help`| (สำหรับผู้เล่นทั่วไป) | แสดงรายการคำสั่งสำหรับแอดมิน |
| `/bsrushards reload` | `bsrushards.admin` | รีโหลดไฟล์ `config.yml` |
| `/bsrushards give <player> <amount>`| `bsrushards.give` | บวกเพิ่มแต้ม Shards ให้ผู้เล่น |
| `/bsrushards set <player> <amount>` | `bsrushards.set` | ตั้งค่าแต้ม Shards ของผู้เล่นเป็นจำนวนที่กำหนด |
| `/bsrushards additem <cost> <slot>` | `bsrushards.shopadmin` | เพิ่มไอเทมที่ถืออยู่ลงในร้านค้า |
| `/bsrushards removeitem <slot>` | `bsrushards.shopadmin` | ลบไอเทมออกจากช่องที่ระบุในร้านค้า |
| `/bsrushards afkpool <...>` | `bsrushards.afkpool.admin` | จัดการโซน AFK (ใช้ `wand`, `create`, `delete`, `list`) |


---

## 🌐 Placeholders

หากติดตั้ง PlaceholderAPI แล้ว สามารถใช้ Placeholder ต่อไปนี้ได้

| Placeholder | คำอธิบาย | ตัวอย่าง |
| :--- | :--- | :--- |
| `%bsrushards_shards%` | แสดง Shards แบบตัวเลขเต็มพร้อมจุลภาค | `1,250` |
| `%bsrushards_shards_formatted%` | แสดง Shards แบบย่อ (k, M, B) | `1.3k` |

---

## 🔧 การตั้งค่า (Configuration)

* `config.yml`: ไฟล์ตั้งค่าหลักของปลั๊กอิน ใช้สำหรับปรับแต่งข้อความ, เสียง, การแจกแต้มอัตโนมัติ, และหน้าตา GUI
* `shop.yml`: ไฟล์ข้อมูลร้านค้า จะถูกจัดการโดยอัตโนมัติผ่านคำสั่งในเกม
* `playerdata.yml`: ไฟล์ข้อมูลแต้มของผู้เล่น จะถูกจัดการโดยอัตโนมัติ
* `afkpool.yml`: ไฟล์ข้อมูลสำหรับโซน AFK จะถูกจัดการโดยอัตโนมัติผ่านคำสั่ง

### Config.yml
```yaml
# --------------------------------------------------- #
#             การตั้งค่าปลั๊กอิน BrsuShards              #
# --------------------------------------------------- #

settings:
  # เปิดหรือปิดระบบแจก Shards อัตโนมัติสำหรับผู้เล่นที่ออนไลน์
  play-shards-enabled: true
  # เวลา (หน่วยเป็นนาที) ในการแจก Shards แต่ละครั้ง
  interval-minutes: 10
  # จำนวน Shards ที่จะแจกให้ผู้เล่นแต่ละคนในแต่ละรอบ
  shards-per-interval: 1

# ตั้งค่า Title และ Subtitle ตอนผู้เล่นเข้าเขต AFK Pool
afk-pool-entry:
  enabled: true
  title: "&b&lเข้าสู่เขต AFK"
  subtitle: "&eคุณจะได้รับรางวัลเมื่อยืนอยู่ในบริเวณนี้"
  # ระยะเวลาการแสดงผล (หน่วยเป็น tick, 20 ticks = 1 วินาที)
  fade-in-ticks: 10
  stay-ticks: 40
  fade-out-ticks: 20

# เสียงเอฟเฟกต์สำหรับการกระทำต่างๆ
sounds:
  open-shop: "minecraft:block.note_block.harp"
  click-buy: "minecraft:ui.button.click"
  buy-success: "minecraft:block.note_block.pling"
  buy-cancel: "minecraft:entity.villager.no"
  buy-fail: "minecraft:entity.villager.no"
  shards-receive: "minecraft:entity.experience_orb.pickup"

# ข้อความทั้งหมดของปลั๊กอินที่สามารถตั้งค่าได้
messages:
  only-in-game: "&cคำสั่งนี้ใช้ได้เฉพาะในเกมเท่านั้น"
  no-permission: "&cคุณไม่มีสิทธิ์ใช้งานคำสั่งนี้!"
  not-enough-shards: "&cคุณมี Shards ไม่เพียงพอ!"
  buy-success: "&aซื้อ %item% สำเร็จ!"
  buy-cancelled: "&cยกเลิกการซื้อแล้ว"
  reload-success: "&aรีโหลดการตั้งค่าสำเร็จ!"
  item-added: "&aเพิ่มไอเทมลงร้านค้าสำเร็จ!"
  set-success: "&aตั้งค่า Shards ของ %player% เป็น %amount% สำเร็จ"
  shards-set: "&eยอด Shards ของคุณถูกตั้งค่าเป็น &f%amount% &eโดยแอดมิน"
  you-have-shards: "&eคุณมี: &f%shards% Shards"
  shop-title: "&6ร้านค้า Shard"
  confirm-title: "&eยืนยันการซื้อ"
  shards-receive: "&aคุณได้รับ &e+%amount% &aShards!"
  shards-receive-bar: "&a+%amount% Shards!"
  # ข้อความสำหรับ AFK Pool
  afkpool-wand-given: "&aคุณได้รับ AFK Pool Wand!"
  afkpool-pos1-set: "&aกำหนดจุดที่ 1 แล้วที่: &e%location%"
  afkpool-pos2-set: "&aกำหนดจุดที่ 2 แล้วที่: &e%location%"
  afkpool-created: "&aสร้าง AFK Pool ชื่อ &e%name% &aสำเร็จ!"
  afkpool-deleted: "&cลบ AFK Pool ชื่อ &e%name% &cสำเร็จแล้ว"
  afkpool-list-header: "&6--- รายชื่อ AFK Pools ---"
  afkpool-list-item: "&e- %name% &7(ให้ &f%shards% &7Shards ทุก &f%time% &7วินาที)"
  afkpool-list-empty: "&cยังไม่มี AFK Pool ใดๆถูกสร้างขึ้น"
  afkpool-not-exist: "&cไม่พบ AFK Pool ที่ชื่อ &e%name%"
  afkpool-name-exists: "&cมี AFK Pool ที่ชื่อ &e%name% &cอยู่แล้ว"
  afkpool-no-selection: "&cคุณต้องกำหนดพื้นที่ด้วย Wand ก่อน! (จุดที่ 1 และ 2)"
  afkpool-invalid-number: "&cค่าเวลาและจำนวนต้องเป็นตัวเลขเท่านั้น!"
  afkpool-reward-message: "&aคุณได้รับ &e+%shards% &aShards จากการ AFK!"
  afkpool-countdown-bar: "&aคุณจะได้รับ &e+%shards% Shards &aจากโซน &b%pool% &aในอีก &f%time% &aวินาที"

# การตั้งค่าสำหรับหน้าต่างกราฟิก (GUI)
shop-ui:
  price-lore: "&eราคา: &f%cost% Shards"
  blank:
    type: GRAY_STAINED_GLASS_PANE
    slots: [0,1,2,3,4,5,6,7,8,9,10,11,13,15,17,18,19,20,21,22,23,24,25,26]
    display: " "
  cannotbuy:
    slots: [1,2,3,4,5,6,7,8,9]
    type: BLACK_STAINED_GLASS_PANE
    display: "&7ไม่มีไว้สำหรับขาย"
    lore:
      - "&8ไอเทมชิ้นนี้ไม่สามารถซื้อได้"
  cancel:
    type: RED_STAINED_GLASS_PANE
    slot: 12
    display: "&cยกเลิก"
  confirm:
    type: GREEN_STAINED_GLASS_PANE
    slot: 16
    display: "&aยืนยัน"
  item-slot: 14
```
# ✍️ ผู้พัฒนา  
Nattapat2871
