Следуйте инструкциям ниже, чтобы настроить и запустить сервис.

Для запуска сервиса необходимо установить следующие инструменты:
Docker
Docker Compose

Для запуска используйте следующую команду:
docker-compose up --build
Эта команда:
Соберет образы для контейнеров
Запустит контейнеры, включая базу данных, приложение и все необходимые сервисы.
Установит все необходимые зависимости и переменные окружения.

После того как контейнеры запустятся, сервис будет доступен по следующему адресу:
API: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui

Следуйте инструкциям ниже, чтобы настроить и запустить сервисДля запуска сервиса необходимо установить следующие инструменты:&nbsp;* Docker&nbsp;* Docker ComposeДля запуска используйте следующую команду: docker-compose up --buildЭта команда: * Соберет образы для контейнеров&nbsp;*Запустит контейнеры, включая базу данных, приложение и все необходимые сервисы* Установит все необходимые зависимости и переменные окружения.После того как контейнеры запустятся, сервис будет доступен по следующему адресу:* API:&nbsp;[http://localhost:8080](http://localhost:8080/" rel="nofollow" style="background-color: transparent; box-sizing: border-box; color: var(--fgColor-accent, var(--color-accent-fg)); text-decoration-line: underline; text-underline-offset: 0.2rem;)*Swagger UI:&nbsp;[http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui" rel="nofollow" style="background-color: transparent; box-sizing: border-box; color: var(--fgColor-accent, var(--color-accent-fg)); text-decoration-line: underline; text-underline-offset: 0.2rem;)
