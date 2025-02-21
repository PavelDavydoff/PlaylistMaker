# PlaylistMaker 🎵

Музыкальный плейлист-мейкер с интеграцией iTunes для поиска и управления треками
<div style="display: flex; justify-content: space-between;">
  <div style="text-align: center;">
    <img src="https://github.com/user-attachments/assets/6f9b9e04-236b-4115-ae24-0a3141553014" width="200" alt="Скриншот 1" />
    <p>Поиск треков</p>
  </div>
  <div style="text-align: center;">
    <img src="https://github.com/user-attachments/assets/4eb04226-1d2c-4448-906f-c3a3264bd04f" width="200" alt="Скриншот 2" />
    <p>Плеер</p>
  </div>
  <div style="text-align: center;">
    <img src="https://github.com/user-attachments/assets/6d7d91d5-af01-4d65-bac3-258959be99a7" width="200" alt="Скриншот 3" />
    <p>Избранное и плейлисты</p>
  </div>
</div>

## ✨ Возможности

- 🔍 Поиск треков через iTunes Music API
- ▶️ Встроенный аудиоплеер с базовым управлением
- ❤️ Добавление треков в избранное
- 🎧 Создание и редактирование плейлистов
- 📱 Адаптивный интерфейс для мобильных устройств
- 📤 Шеринг плейлистами через мессенджеры
- 📁 Локальное кэширование данных (Room Database)

## 🛠 Технологии и архитектура

### Архитектура
- **Clean Architecture** с многослойной структурой
- **MVVM** (Model-View-ViewModel)

### Основной стек
![Kotlin](https://img.shields.io/badge/-Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Retrofit](https://img.shields.io/badge/-Retrofit-6DB33F?logo=square&logoColor=white)
![Room](https://img.shields.io/badge/-Room-4285F4?logo=google-cloud&logoColor=white)
![Jetpack Navigation](https://img.shields.io/badge/-Navigation-4285F4?logo=android&logoColor=white)
![Koin](https://img.shields.io/badge/-Koin-FF6D00?logo=koin&logoColor=white)

### Используемые компоненты
- **Retrofit2** - для сетевых запросов
- **Room** - локальная база данных
- **Jetpack Navigation** - навигация между экранами
- **Koin** - dependency injection
- **ViewBinding** - работа с view
- **Coroutines** - асинхронные операции
