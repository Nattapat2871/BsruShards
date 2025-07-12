[English Version](README.md)

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
* **🛒 ร้านค้า GUI:** ผู้เล่นสามารถใช้คำสั่ง `/shardsshop` เพื่อเปิดร้านค้าสำหรับแลกไอเทมด้วยแต้ม Shards
* **✅ หน้าต่างยืนยันการซื้อ:** มี UI ยืนยันก่อนการซื้อไอเทมเพื่อป้องกันการกดพลาด
* **👨‍💻 คำสั่งแอดมิน:** จัดการผู้เล่นและร้านค้าได้ง่ายๆ ผ่านคำสั่ง `/bsrushards`
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
* `shop.yml`: ไฟล์ข้อมูลร้านค้า จะถูกจัดการโดยอัตโนมัติผ่านคำสั่ง `/bsrushards additem`
* `playerdata.yml`: ไฟล์ข้อมูลแต้มของผู้เล่น จะถูกจัดการโดยอัตโนมัติ

### Config.yml
```yaml
# --------------------------------------------------- #
#             การตั้งค่าปลั๊กอิน BrsuShards              #
# --------------------------------------------------- #

settings:
  # เปิดหรือปิดระบบแจก Shards อัตโนมัติสำหรับผู้เล่นที่ออนไลน์
  # ตั้งค่าเป็น 'true' เพื่อเปิดใช้งาน, 'false' เพื่อปิดใช้งาน
  play-shards-enabled: true
  
  # เวลา (หน่วยเป็นนาที) ในการแจก Shards แต่ละครั้ง
  interval-minutes: 10
  
  # จำนวน Shards ที่จะแจกให้ผู้เล่นแต่ละคนในแต่ละรอบ
  shards-per-interval: 1

# เสียงเอฟเฟกต์สำหรับการกระทำต่างๆ
# คุณสามารถค้นหารายชื่อเสียงได้จาก Minecraft Wiki
sounds:
  # เสียงเมื่อผู้เล่นเปิดร้านค้า
  open-shop: "minecraft:block.note_block.harp"
  
  # เสียงเมื่อผู้เล่นคลิกไอเทมในร้านค้า
  click-buy: "minecraft:ui.button.click"
  
  # เสียงเมื่อซื้อของสำเร็จ
  buy-success: "minecraft:block.note_block.pling"
  
  # เสียงเมื่อยกเลิกการซื้อ
  buy-cancel: "minecraft:entity.villager.no"
  
  # เสียงเมื่อการซื้อล้มเหลว (เช่น แต้มไม่พอ)
  buy-fail: "minecraft:entity.villager.no"
  
  # เสียงเมื่อได้รับ Shards
  shards-receive: "minecraft:entity.experience_orb.pickup"

# ข้อความทั้งหมดของปลั๊กอินที่สามารถตั้งค่าได้
# คุณสามารถใช้โค้ดสีของ Minecraft ด้วยเครื่องหมาย '&'
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
  
  # %shards% จะถูกแทนที่ด้วยจำนวน Shards ของผู้เล่น (เช่น 1,000)
  you-have-shards: "&eคุณมี: &f%shards% Shards"
  
  # ชื่อของหน้าต่างร้านค้าหลัก
  shop-title: "&6ร้านค้า Shard"
  
  # ชื่อของหน้าต่างยืนยันการซื้อ
  confirm-title: "&eยืนยันการซื้อ"
  
  # ข้อความในแชทเมื่อได้รับ Shards. %amount% คือจำนวนที่ได้รับ
  shards-receive: "&aคุณได้รับ &e+%amount% &aShards!"
  
  # ข้อความบน Action bar เมื่อได้รับ Shards
  shards-receive-bar: "&a+%amount% Shards!"

# การตั้งค่าสำหรับหน้าต่างกราฟิก (GUI)
shop-ui:
  # บรรทัดคำอธิบาย (lore) ที่จะถูกเพิ่มเข้าไปในไอเทมร้านค้าเพื่อแสดงราคา
  # %cost% จะถูกแทนที่ด้วยราคาของไอเทม
  price-lore: "&eราคา: &f%cost% Shards"
  
  # ไอเทมที่ใช้เป็นช่องว่างสำหรับตกแต่งพื้นหลัง GUI
  blank:
    type: GRAY_STAINED_GLASS_PANE
    # ช่องที่จะเติมด้วยไอเทมนี้
    slots: [0,1,2,3,4,5,6,7,8,9,10,11,13,15,17,18,19,20,21,22,23,24,25,26]
    display: " "
    
  # ไอเทมที่ใช้สำหรับช่องที่ตกแต่งเท่านั้นและไม่สามารถซื้อได้
  cannotbuy:
    # รายชื่อช่องที่จะเติมด้วยไอเทมนี้ในร้านค้าหลัก
    slots: [1,2,3,4,5,6,7,8,9]
    type: BLACK_STAINED_GLASS_PANE
    display: "&7ไม่มีไว้สำหรับขาย"
    lore:
      - "&8ไอเทมชิ้นนี้ไม่สามารถซื้อได้"
      
  # ปุ่ม 'ยกเลิก' ในหน้าต่างยืนยันการซื้อ
  cancel:
    type: RED_STAINED_GLASS_PANE
    # ช่องสำหรับปุ่มยกเลิก
    slot: 12
    display: "&cยกเลิก"
    
  # ปุ่ม 'ยืนยัน' ในหน้าต่างยืนยันการซื้อ
  confirm:
    type: GREEN_STAINED_GLASS_PANE
    # ช่องสำหรับปุ่มยืนยัน
    slot: 16
    display: "&aยืนยัน"
    
  # ช่องที่ใช้แสดงไอเทมที่กำลังจะซื้อในหน้าต่างยืนยัน
  item-slot: 14
```

---

## ✍️ ผู้พัฒนา

พัฒนาโดย: **Nattapat2871**
[GitHub Project](https://github.com/nattapat2871/bsrushards)