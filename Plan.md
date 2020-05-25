# План тестирования
Ниже приведён план тестирования сервиса покупки туров, взаимодействующего с СУБД и API Банка. <br />
<br />
Тестирование выполняется в рамках дипломного проекта Анастасии Суриной на курсе Тестировщик ПО от Нетологии в мае 2020 года.
### Перечень используемых инструментов
- Git и Github
  - Удобная система контроля версий для хранения кода.
- Docker
  - Docker позволяет облегчить настройку необходимых для запуска и работы сервиса зависимостей, таких как MySQL, PostgreSQL, Node, скоординировать их взаимодействие.
  - Планируется прибегнуть к докеризации приложения: создать Dockerfile, который будет запускать контейнер с симулятором банковских сервисов, два контейнера с разными СУБД и два инстанса исходного Java-приложения, развернутого на разных портах (по одному на каждую БД).
  - Таким образом будет достигнута высокая степень автоматизации: настройка системы перед запуском автотестов сведётся к запуску Dockerfile. 
- Java 11
- Автотесты под управлением Gradle на базе библиотеки JUnit5
- Фреймворк для автоматизированного тестирования веб-приложений Selenide
  - Автоматически скачивает драйвер для браузера.
  - Упрощает поиск элементов.
  - Удобен в использовании.
- Веб-браузер Google Chrome
- Lombok
  - Упрощает создание data-классов, таких как PageObject, и классов для хранения тестовых данных.
  - Это позволит изолировать логику получения/генерации данных, логику взаимодействия с веб-страницей и логику взаимодействия с базой данных от логики самих автотестов.
- Mysql-connector
  - Позволяет прямо из тестов взаимодействовать с базой данных.
- Система репортинга Allure.
  - Удобно интегрируется со всеми остальными инструментами.
  - Позволяет получать развернутый отчёт со скриншотами.
### Перечень автоматизируемых сценариев

##### Подготовительные шаги
1. Запустить контейнеры с симулятором банковских сервисов, двумя СУБД и двумя СУТ на разных портах (обращающимся каждый к своей БД) из Dockerfile.
2. Открыть в браузере первое СУТ во вкладке 1: http://localhost:8080/
3. Открыть в браузере второе СУТ во вкладке 2: http://localhost:8081/
4. Выполнить позитивные и негативные сценарии в обеих вкладках по два раза: для покупки по карте и покупки в кредит.
5. Выбрать покупку в кредит или покупку по карте, нажав, соответственно, на кнопку "Купить" или "Купить в кредит" для начала тестирования.
 
##### Позитивные
Успешная операция<br />

Шаги:<br />

1. В поле "Номер карты" ввести "4444 4444 4444 4441".
2. В поле "Год" ввести две последние цифры любого из последующих шести лет с текущей даты (например, "22").
3. В поле "Месяц" ввести цисло от 01 до 12 (например, "08").
4. В поле "Владелец" ввести имя и фамилию латинскими буквами (например, "Vasya Ivanov").
5. В поле "CVC/CVV" ввести любые три цифры (например, "999").
6. Нажать кнопку "Продолжить".
7. Запросить статус последней операции в базе данных app (в соответствующей СУБД).
  - Для оплаты по карте:
     ``` 
     SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1;
     ```
  - Для оплаты в кредит:
     ``` 
     SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1;
     ```
Ожидаемый результат: <br />
- Поведение веб-интерфейса: уведомление "Успешно! Операция одобрена банком" в правом верхнем углу.
- Статус операции в базе данных: "APPROVED".

##### Негативные
- Невалидный номер карты из списка.
 1. В поле "Номер карты" ввести "4444 4444 4444 4442".
 2. В поле "Год" ввести две последние цифры любого из последующих шести лет с текущей даты (например, "22").
 3. В поле "Месяц" ввести цисло от 01 до 12 (например, "08").
 4. В поле "Владелец" ввести имя и фамилию латинскими буквами (например, "Vasya Ivanov").
 5. В поле "CVC/CVV" ввести любые три цифры (например, "999").
 6. Нажать кнопку "Продолжить".
 7. Запросить статус последней операции в базе данных app (в соответствующей СУБД).
     - Для оплаты по карте:
        ``` 
        SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1;
        ```
     - Для оплаты в кредит:
        ``` 
        SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1;
        ```
Ожидаемый результат: <br />
Поведение веб-интерфейса: уведомление "Ошибка! Банк отказал в проведении операции" в правом верхнем углу.<br />
Статус операции в базе данных: "DECLINED". <br />
- Невалидный номер карты не из списка.
 1. В поле "Номер карты" ввести "8888 8888 8888 8888".
 2. В поле "Год" ввести две последние цифры любого из последующих шести лет с текущей даты (например, "22").
 3. В поле "Месяц" ввести цисло от 01 до 12 (например, "08").
 4. В поле "Владелец" ввести имя и фамилию латинскими буквами (например, "Vasya Ivanov").
 5. В поле "CVC/CVV" ввести любые три цифры (например, "999").
 6. Нажать кнопку "Продолжить".
 7. Запросить статус последней операции в базе данных app (в соответствующей СУБД).
     - Для оплаты по карте:
        ``` 
        SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1;
        ```
     - Для оплаты в кредит:
        ``` 
        SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1;
        ```
Ожидаемый результат: <br />
Поведение веб-интерфейса: уведомление "Ошибка! Банк отказал в проведении операции" в правом верхнем углу.<br />
Статус операции в базе данных: "DECLINED". <br />
   	
- Карта с истекшим сроком (годом) действия.<br />
 1. В поле "Номер карты" ввести "4444 4444 4444 4441".
 2. В поле "Год" ввести две последние цифры любого из прошедших шести лет с текущей даты (например, "18").
 3. В поле "Месяц" ввести цисло от 01 до 12 (например, "08").
 4. В поле "Владелец" ввести имя и фамилию латинскими буквами (например, "Vasya Ivanov").
 5. В поле "CVC/CVV" ввести любые три цифры (например, "999").
 6. Нажать кнопку "Продолжить".<br />
 
Ожидаемый результат: <br />
Поведение веб-интерфейса: уведомление "Истёк срок действия карты" под полем "Год". Форма не отправлена.<br />

- Неверный формат полей.<br />
 1. В поле "Номер карты" ввести "4444".
 2. В поле "Год" ввести одну цифру текущего года (например, "2").
 3. В поле "Месяц" ввести одну цифру текущего года (например, "0").
 4. В поле "Владелец" ввести набор чисел  (например, "1234").
 5. В поле "CVC/CVV" ввести любые две цифры (например, "99").
 6. Нажать кнопку "Продолжить". <br />
 
Ожидаемый результат: <br />
Поведение веб-интерфейса: уведомление "Неверный формат" под всеми шестью полями. Форма не отправлена.<br />
    
- Кириллица в поле "Владелец". <br />
 1. В поле "Номер карты" ввести "4444 4444 4444 4441".
 2. В поле "Год" ввести две последние цифры любого из следующих шести лет с текущей даты (например, "22").
 3. В поле "Месяц" ввести цисло от 01 до 12 (например, "08").
 4. В поле "Владелец" ввести имя и фамилию латинскими буквами (например, "Исаева Алёна").
 5. В поле "CVC/CVV" ввести любые три цифры (например, "999").
 6. Нажать кнопку "Продолжить".<br />
 
Ожидаемый результат: <br />
Поведение веб-интерфейса: уведомление "Неверный формат" под полем "Владелец". Форма не отправлена.<br />
  
- Попытка отправки формы с пустыми полями.<br />
 1. Оставить все шесть полей пустыми.
 6. Нажать кнопку "Продолжить".<br />
 
Ожидаемый результат: <br />
Поведение веб-интерфейса: уведомление "Поле обязательно для заполнения" под всеми шестью полями. Форма не отправлена.<br />
    	
        	
Сценарии повторяются дважды: для покупки по карте и покупки в кредит, на разных окружениях. <br />
##### Шаги по окончанию тестирования
Необходимо очистить все таблицы в базах данных.
    	
### Перечень и описание возможных рисков при автоматизации
- Тестированию подвергается не финальная версия сервиса, а его демо-версия. Если в финальную версию будут внесены изменения, тестирование текущей версии может стать неактуальным.
- Отсутствует чёткое ТЗ, а значит, есть риск неправильно определить ожидаемое поведение сервиса (например, корректность или некорректность заполнения поля "Владелец" кириллицей).
### Интервальная оценка с учётом рисков (в часах)
- 10 часов.
### План сдачи работ (когда будут авто-тесты, результаты их прогона и отчёт по автоматизации)
- Авто-тесты будут готовы не позднее 30.05.
- Отчётные документы по итогам тестирования будут готовы не позднее 02.06.
