# Yapılandırma

## Yetkili Sohbet
```yaml
yetkili-chat:
  format: "&8[&cYetkili&8] &7{player}: &f{message}"
```

## Yetkili Süre ve Discord
```yaml
yetkili-sure:
  title: "&8&lYetkili Süre Sıralaması"
  size: 54
  weekly-reset:
    day: "SUNDAY"
    hour: 23
    minute: 0
  discord:
    enabled: false
    webhook-url: "YOUR_DISCORD_WEBHOOK_URL"
    channel-id: "YOUR_CHANNEL_ID"
    timeout: 5000
    retry-attempts: 3
    debug: false
```

## İzleme
```yaml
izleme:
  max-distance: 15
  spectator-mode: "SPECTATOR"
  return-mode: "SURVIVAL"
```

## Kullanıcı Menüsü
```yaml
kullanici-menu:
  title: "&8&l{player} Bilgileri"
  size: 54
  items:
    dupe-ip:
      slot: 12
    cezalar:
      slot: 14
    sohbet-kayitlari:
      slot: 16
```

## Yetkililer Menüsü
```yaml
yetkililer-menu:
  title: "&8&lYetkililer"
  size: 54
  permission: "klas.yetkililer"
```

Not: `webhook-url` gibi gizli bilgileri repoya koymayın. Sunucu tarafında doldurun.