package test.server;
/*
Тестовое задание "Java разработчик" v.1

У вас два устройства на Linux. Устройство А выполняет роль клиента, устройство Б выполняет роль сервера. На устройстве А установлен браузер: Firefox.

Клиент. UI на Swagger: Config, Старт/Стоп, Log. В кофиге указывает ip и порт сервера и значение частоты X. В Log выбирается параметр сортировки со значениями T, X, Y.

Сервер. UI на Swagger: Config для выбора порта подключения клиента, Запуск/Отключения прослушивания порта.

Логика.
При применении Старт клиент запускает на сервере следующий функционал: сервер открывает три независимых окна Firefox, которые не должны визуально пересекаться между собой. Сервер на уровне операционной системы подключает 3 реальных указателя мыши и устанавливает каждый из них в центр одного из браузеров, затем ждёт 5 секунд и начинает двигать указатель мыши по круговой траектории, не выходя указателем мыши за границы соответствующего браузера. Отправка координат указателя мыши в ОС должна быть не менее X раз в секунуду.

Одновременно с этим сервер отправляет клиенту координаты-время одного из указателей по протоколу websocket, а клиент записывает их в PostgreSQL. Процесс продолжается на сервере до тех пор, пока на клиенте не будет применён Стоп.

При запуске Log создаётся txt файл с логами координат-времени, которые отсортированы в соответствии с параметром: T - по времени, X - по значнию x, Y - по значению y.

__

Для работы на уровне операционной системы допустимо использовать C++ или Rust.

__

Что хочется увидеть в проекте?

1. Понимание модульности в проекте(в нашем случае это два приложения рамках одного проекта клиент и сервер).
2. Применения шаблона проектирования Singleton.
3. Умение рефакторить собственный код.
4. Использование DI решений.
5. Отсутствие закомментированного кода.
6. Разделение сервисов на слои.

Дедлайн - неделя

Результат отправлять ссылкой на git
 */

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static java.lang.Thread.sleep;

@Controller
public class ServerController {
    private App app;
    Double x = 8.0;
    private Double period = 1/x;
    private Double angle = 0.0;

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public Message mainProcess(String message) throws Exception {
        switch (message){
            case "start"-> {
                this.app = new App();
                sleep(5000);
                return new Message(getCursorCoordinates());
            }
            case "stop"->{
                this.app.closeAll();
            }
            case "Got"->{
                return new Message(getCursorCoordinates());
            }
        }
        return null;
    }

    private Integer[] getCursorCoordinates() throws InterruptedException {
        Integer[] results = app.getCoordinates(this.period, this.angle);
        angle += period;
        return results;
    }
}