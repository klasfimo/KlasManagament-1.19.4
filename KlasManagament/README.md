## Klas Management Plugin

Klas Management, Minecraft sunucularında yetkili yönetimini standartlaştırmak ve hızlandırmak için geliştirilmiş, modüler ve performans odaklı bir yönetim eklentisidir. Yetkili sohbeti, süre takip ve sıralama, oyuncu bilgi ekranları, izleme, Discord bildirimi ve veri kalıcılığı gibi temel ihtiyaçları tek bir pakette toplar.

### Genel Bakış
- Paper/Spigot 1.19.4 ve Java 17+ ile uyumlu
- Modüler mimari, ayrı yöneticiler (Manager) ve dinleyiciler (Listener)
- Kalıcı veri saklama (YAML)
- Opsiyonel entegrasyonlar: PlaceholderAPI, LiteBans, LuckPerms
- GUI menüler ve tıklama etkileşimleri

### Özellikler
- Yetkili Sohbet: Sunucu genel sohbetinden ayrışan, yalnızca yetkililerin görebildiği kanal.
- Yetkili Süre Sistemi: Kalıcı ve haftalık süre birikimi, sıralama menüleri, haftalık otomatik/manuel sıfırlama, opsiyonel Discord sıralama bildirimi.
- Kullanıcı Bilgileri: Dupe IP kullanıcıları, ceza geçmişi (LiteBans), sohbet kayıtları (en son 100 mesaj, tarih/saat damgalı).
- İzleme: Hedef oyuncuyu SPECTATOR modunda takip etme, mesafe kontrolü ve otomatik geri çağırma, tek komutla başlat/durdur.
- Yetkililer Menüsü: Özel bir izne sahip kullanıcıları listeleyen menü (çevrimiçi/dışı, dünya, ping, kalıcı/haftalık süre özetleri).
- Kalıcı Veri: Tüm kritik veriler sunucu yeniden başlatmalarında korunur (YAML dosyaları).
- Discord Webhook: Haftalık sıralama sıfırlamalarında Discord’a özet gönderimi (asenkron, timeout ve yeniden deneme mekanizmalı).

### Komutlar
| Komut | Açıklama | İzin | Kısayol/Alias |
|---|---|---|---|
| `/yetkilichat` | Yetkili sohbetini aç/kapat | `klas.yetkilichat` | `yc` |
| `/yetkilisure` | Yetkili süre menüsünü aç | `klas.yetkilisure` | `ys` |
| `/izle <oyuncu>` | Oyuncuyu SPECTATOR ile izle/çık | `klas.izle` | - |
| `/kullanici <oyuncu>` | Oyuncu bilgi menüsünü aç | `klas.kullanici` | `kullanıcı` |
| `/yetkililer` | Yetkililer menüsünü aç | `klas.yetkililer` | - |
| `/klasmanagement reload` | Eklentiyi ve konfigleri canlı yeniden yükle | `klas.reload` | `km reload` |
| `/yetkilisuresifirla` | Haftalık yetkili sürelerini manuel sıfırla (Discord bildirimi tetikler) | `klas.yetkilisure.reset` | - |

### İzinler
- `klas.yetkilichat` – Yetkili sohbeti kullanma
- `klas.yetkilisure` – Yetkili süre menülerini kullanma
- `klas.izle` – İzleme komutunu kullanma
- `klas.kullanici` – Oyuncu bilgi menülerini kullanma
- `klas.yetkililer` – Yetkililer menüsüne erişim
- `klas.reload` – Eklentiyi canlı yeniden yükleme
- `klas.yetkilisure.reset` – Haftalık süreleri manuel sıfırlama
- `klas.admin` – Üst izin; tüm yetkileri kapsar

### Konfigürasyon Özeti (`config.yml`)
Temel anahtarlar ve işlevleri:

- Yetkili sohbet biçimi
```yaml
yetkili-chat:
  format: "&8[&cYetkili&8] &7{player}: &f{message}"
```

- Yetkili süre menüsü, haftalık sıfırlama ve Discord
```yaml
yetkili-sure:
  title: "&8&lYetkili Süre Sıralaması"
  size: 54
  weekly-reset:
    day: "SUNDAY"   # Sıfırlama günü
    hour: 23         # Saat (0-23)
    minute: 0        # Dakika (0-59)
  discord:
    enabled: false
    webhook-url: "YOUR_DISCORD_WEBHOOK_URL"
    channel-id: "YOUR_CHANNEL_ID"
    timeout: 5000
    retry-attempts: 3
    debug: false
  items:
    kalici-siralama:
      slot: 20
      material: "PAPER"
      name: "&6&lKalıcı Sıralama"
      lore: ["&7Kalıcı toplamlar", "&eTıkla ve görüntüle"]
    haftalik-siralama:
      slot: 24
      material: "PAPER"
      name: "&a&lHaftalık Sıralama"
      lore: ["&7Haftalık toplamlar", "&eTıkla ve görüntüle"]
```

- İzleme davranışı
```yaml
izleme:
  max-distance: 15
  spectator-mode: "SPECTATOR"
  return-mode: "SURVIVAL"
```

- Kullanıcı menüsü ve öğeleri
```yaml
kullanici-menu:
  title: "&8&l{player} Bilgileri"
  size: 54
  items:
    dupe-ip:
      slot: 12
      material: "GLOBE_BANNER_PATTERN"
      name: "&c&lDupe IP"
      lore: ["&7Aynı IP'yi kullananlar", "&eTıkla ve görüntüle"]
    cezalar:
      slot: 14
      material: "REDSTONE"
      name: "&4&lCezalar"
      lore: ["&7Geçmiş cezalar", "&eTıkla ve görüntüle"]
    sohbet-kayitlari:
      slot: 16
      material: "BOOK"
      name: "&a&lSohbet Kayıtları"
      lore: ["&7Son 100 mesaj", "&eTıkla ve görüntüle"]
```

- Yetkililer menüsü
```yaml
yetkililer-menu:
  title: "&8&lYetkililer"
  size: 54
  permission: "klas.yetkililer"
```

- Mesajlar (örnek)
```yaml
messages:
  yetkili-chat:
    enabled: "&aYetkili sohbeti aktif!"
    disabled: "&cYetkili sohbeti kapalı!"
    no-permission: "&cBu komutu kullanmaya yetkiniz yok!"
  izleme:
    started: "&a{player} izlenmeye başlandı!"
    stopped: "&cİzleme sonlandırıldı!"
    player-not-found: "&cBelirtilen oyuncu bulunamadı!"
    too-far: "&cOyuncudan uzaklaştınız, geri ışınlanıyorsunuz..."
  kullanici:
    player-not-found: "&cBelirtilen oyuncu bulunamadı!"
    no-data: "&cBu oyuncu hakkında veri bulunamadı!"
```

### Kalıcı Veri Dosyaları
Eklentinin veri klasörü altında YAML formatında saklanır (sunucu yeniden başlatmalarında korunur):

```
plugins/KlasManagament/
├─ kalici_sureler.yml       # Kalıcı (hiç sıfırlanmayan) yetkili süreleri (saniye)
├─ haftalik_sureler.yml     # Haftalık yetkili süreleri (saniye)
├─ last_reset.yml           # Haftalık sıfırlama zaman bilgisi
├─ player_messages.yml      # Oyuncuların son 100 sohbet mesajı (timestamp ile)
└─ ip_alts.yml              # IP bazlı ilişkilendirilmiş oyuncu isimleri
```

### Entegrasyonlar (Opsiyonel)
- PlaceholderAPI: Dinamik metin/placeholder desteği. Eklenti yoksa temel yer tutucular çalışır; varsa geniş placeholder seti kullanılabilir.
- LiteBans: Ceza sorgulama ve geçmiş gösterimi için. Eklenti yoksa örnek/boş veri dönebilir.
- LuckPerms: Yetki grubu/rol bilgisi ve detaylı izin yönetimi için. Eklenti yoksa temel izin kontrolleri uygulanır.

### Uyumluluk
- Minecraft: 1.19.4
- Java: 17+
- Sunucu: Paper/Spigot 1.19.4+

### Kurulum
1. Derlenmiş JAR dosyasını `plugins` klasörüne kopyalayın.
2. Sunucuyu başlatın, `config.yml` ve veri klasörü otomatik oluşur.
3. `config.yml` üzerinde ihtiyaçlarınıza göre düzenleme yapın.
4. Yetkileri (LuckPerms vb.) dağıtın.

### Derleme (Geliştiriciler İçin)
- Gradle ile derleme: `./gradlew build`
- Hedef JAR: `build/libs/`

### Performans Notları
- Süre toplama görevi saniyede bir çalışır ve yalnızca ilgili izne sahip çevrimiçi oyuncuların sayaçlarını artırır.
- Discord webhook işlemleri ana iş parçacığını engellememesi için asenkron olarak yürütülür ve zaman aşımı/yeniden deneme mekanizması içerir.

### Güvenlik ve Yetkilendirme
- Tüm komutlar ayrık izinlerle korunur ve `klas.admin` üst izni üzerinden merkezi şekilde devreye alınabilir.
- GUI etkileşimleri yalnızca eklentinin oluşturduğu menü başlıklarında yakalanır, diğer envanterleri etkilemez.

### Sık Karşılaşılan Sorular
- Haftalık sıfırlama zamanı nasıl ayarlanır? `yetkili-sure.weekly-reset` altında gün/saat/dakika alanlarını düzenleyin.
- Discord mesajı gelmiyor, neden? `enabled`, `webhook-url` ve ağ erişimini kontrol edin; `timeout` ve `retry-attempts` değerlerini artırmayı deneyin.
- PlaceholderAPI yüklü değil, çalışır mı? Evet, temel yer tutucular çalışır; PlaceholderAPI yüklüyse geniş placeholder seti devreye girer.

### Sürüm
- v1.0 – İlk kararlı sürüm, temel yetkili yönetimi, GUI menüler, izleme, kalıcı veri, isteğe bağlı entegrasyonlar ve Discord bildirimi. 