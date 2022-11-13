Перечень автоматизируемых сценариев

-покупка по дебетовой карте (позитивный кейс) + (негативный кейс)
-покупка за кредит (позитивный кейс) + (негативный кейс)

Перечень используемых инструментов с обоснованием выбора

-Java Gradle автотест с использованием Selenium Web Driver. Необходимо так же написать API тесты (чтобы проверить корректность запросов и ответов от БД)

Риски

Сперва тестирование необходимо провести вручную чтобы понять тестовые случаи и тестовые пользовательские сценарии. А так же техники тест дизайна. На это уйдет большое количество времени

На ручное тестирование с использованием тест дизайна необходимо время. Тем более нужно будет заранее подготовить тестовую среду. Так как само приложение мы сможем запустить после того как подготовим небольшой шаблон в Java для запуска приложения. На это может уйти 2-3 часа + тест дизайн с ручным тестированием +4-5 часов. После этого подключение БД в проекту это еще +2-3 часа. И написание автотестов на это может уйти целый день
На текущей неделе планирую протестировать в ручную и составить тест план + тест кейсы и подготовить подложку для запуска джарника(тест кейсы как позитивные так и негативные)

На следующей неделе планирую прописать автотест. На третьей неделе подготовить отчет о тестировании а так же если будут баги то баг репорты.