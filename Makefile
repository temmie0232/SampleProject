.PHONY: up down logs ps backend-test frontend-install frontend-build

up:
	docker-compose up --build -d

down:
	docker-compose down -v

logs:
	docker-compose logs -f

ps:
	docker-compose ps

backend-test:
	JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 GRADLE_USER_HOME=$(PWD)/.gradle ./backend/gradlew -p backend --project-cache-dir $(PWD)/.gradle-project-cache --no-daemon test

frontend-install:
	cd frontend && npm install

frontend-build:
	cd frontend && npm run build
