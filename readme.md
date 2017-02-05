# Voting System for Restaurant lunches
#### Тестовый проект

## Задание
Design and implement a JSON API using Hibernate/Spring/SpringMVC (or Spring-Boot) **without frontend**.

The task is:

Build a voting system for deciding where to have lunch.

 * 2 types of users: admin and regular users
 * Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
 * Menu changes each day (admins do the updates)
 * Users can vote on which restaurant they want to have lunch at
 * Only one vote counted per user
 * If user votes again the same day:
    - If it is before 11:00 we asume that he changed his mind.
    - If it is after 11:00 then it is too late, vote can't be changed

Each restaurant provides new menu each day.

As a result, provide a link to github repository. It should contain the code, README.md with API documentation and couple curl commands to test it.

P.S.: Make sure everything works with latest version that is on github :)

P.P.S.: Asume that your API will be used by a frontend developer to build frontend on top of that.

## Реализация

### Используемый стек технологий

*   Spring Boot 
*   Spring Data REST
*   Spring Data JPA
*   Spring Security
*   H2
*   MySQL
*   JUnit
*   Lombok
*   Maven

Приложение разработано на основе Spring Data REST, что позволяет реализовать REST API с использованием технологии [HATEOAS](http://spring-projects.ru/understanding/hateoas/) - Hypermedia as the Engine of Application State.  

### Структура данных

![Структура данных][data-structure]
[data-structure]: data-structure.png "Структура данных"

#### Poll - Опрос
Центральный объект приложения - описывает голосование за лучшее меню/ресторан в течение одного дня. 
Содержит ссылки на **список** объектов типа Меню (см. ниже), за которые идет голосование, и ссылку на победившее Меню (для завершенного опроса). 

В течение дня может быть только один Опрос.
Опрос не может содержать несколько Меню одного и того же Ресторана.
Нельзя создать Опрос за прошедший день или за текущий день, если голосование уже завершилось (по умолчанию 11:00).
Нельзя удалить или изменить опрос, в котором пользователи уже голосовали.

Голосовать можно только в рамках текущего незавершенного Опроса.
Опрос открыт до установленного времени (по умолчанию - до 11:00).
Текущим опросом считается первый незаврешенный либо последний завершенный Опрос. 
  
#### Menu - Меню
Содержит ссылку на объект Ресторан (см. ниже) и на **список** объектов MenuItem (пункт меню).

Одно и тоже меню может входить в разные опросы. 

#### MenuItem - Пункт меню
Описывает блюдо и его цену.

Блюда существуют только в пределах Меню, в которое они входят. 

#### Restaurant - Ресторан
Описывает Ресторан (название, адрес и т.п.).

Нельзя создать два ресторана с одним и тем же названием.

#### User - Пользователь
Описывает пользователя данного сервиса. Может быть обычным пользователем или администратором.

Нельзя зарегистрировать двух пользователей с одним и тем же адресом электронной почты.

Администраторы могут создавать/редактировать: Опросы, Меню, Блюда, Рестораны и др. пользователей, и выполнять действия доступные обычным Пользователям.

Пользователи могут: зарегистрироваться на сервисе, редактировать свой профиль и голосовать за выбранное Меню/Ресторан.

#### Vote - Голос
Предназначен для регистрации голоса Пользователя, который он отдал за выбранное Меню/Ресторан в течение текущего незавершенного Опроса.

Голосовать можно только в течение незавершенного текущего Опроса.
Если Пользователь голосует второй раз в течение открытого Опроса, то его предыдущий голос перезаписывается.

### Поведение

#### Старт приложения
1.  
2.  
3.  

#### Начало нового дня (по умолчанию - 9:00)
1.  
2.  
3.  

#### Завершение голосования (по умолчанию - 11:00)
1.  
2.  
3.  

### Описание интерфейса (API)

#### /api

Выводит список доступных объектов: Опросы, Меню, Рестораны, Пользователи

Команды:
*   **GET** - Доступно всем посетителям

    GET [http://localhost:8080/api]()
 
*Response body:*

    {
      "_links": {
        "users": {
          "href": "http://localhost:8080/api/users{?page,size,sort}",
          "templated": true,
          "title": "Пользователи"
        },
        "menus": {
          "href": "http://localhost:8080/api/menus{?page,size,sort,projection}",
          "templated": true,
          "title": "Список меню"
        },
        "restaurants": {
          "href": "http://localhost:8080/api/restaurants{?page,size,sort}",
          "templated": true,
          "title": "Рестораны"
        },
        "polls": {
          "href": "http://localhost:8080/api/polls{?page,size,sort}",
          "templated": true,
          "title": "Опросы"
        },
        "currentPoll": {
          "href": "http://localhost:8080/api/polls/current",
          "title": "Текущий опрос"
        },
        "userProfile": {
          "href": "http://localhost:8080/api/userProfile",
          "title": "Профиль пользователя"
        },
        "profile": {
          "href": "http://localhost:8080/api/profile"
        }
      }
    }

#### /api/userProfile

Работа с профилем рядового пользователя.

Команды:
*   **GET** - Получение профиля. Доступно авторизованному пользователю.

    GET [http://localhost:8080/api/userProfile]()

    *Response body:*

        {
          "name": "Frodo Baggins",
          "password": "",
          "email": "frodo@restvotes.com",
          "_links": {
            "userProfile": {
              "href": "https://localhost:8080/api/userProfile",
              "title": "Профиль пользователя"
            }
          }
        }

*   **POST** - Регистрация нового пользователя. Доступно всем посетителям
    
    POST [http://localhost:8080/api/userProfile]()

    *Request body:*

        {
          "name": "Givi Zurabovich",
          "password": "123456",
          "email": "givi@restvotes.com",
        }

*   **PUT** - Редактирование профиля. Доступно авторизованному пользователю (тело запроса аналогично POST).
    
    PUT [http://localhost:8080/api/userProfile]()

#### /api/polls

Работа с Опросами.

Команды:
*   **GET** - Простмотр всех опросов. Доступно авторизованному пользователю.

    GET [http://localhost:8080/api/polls]()
        
        *Response body:*
        
        {
          "_embedded": {
            "polls": [
              {
                "date": "2017-02-06",
                "finished": false,
                "current": true,
                "_links": {
                  "self": {
                    "href": "https://localhost:8080/api/polls/2017-02-06"
                  }
                }
              },
              {
                "date": "2017-02-04",
                "finished": true,
                "current": false,
                "_links": {
                  "self": {
                    "href": "https://localhost:8080/api/polls/2017-02-04"
                  },
                  "winner": {
                    "href": "https://localhost:8080/api/polls/2017-02-04/menus/42",
                    "title": "Победитель"
                  }
                }
              },
              {
                "date": "2017-02-03",
                "finished": true,
                "current": false,
                "_links": {
                  "self": {
                    "href": "https://localhost:8080/api/polls/2017-02-03"
                  },
                  "winner": {
                    "href": "https://localhost:8080/api/polls/2017-02-03/menus/12",
                    "title": "Победитель"
                  }
                }
              }
            ]
          },
          "_links": {
            "self": {
              "href": "https://localhost:8080/api/polls"
            },
            "currentPoll": {
              "href": "https://localhost:8080/api/polls/current",
              "title": "Текущий опрос"
            },
            "profile": {
              "href": "https://localhost:8080/api/profile/polls"
            },
            "search": {
              "href": "https://localhost:8080/api/polls/search",
              "title": "Поиск"
            }
          },
          "page": {
            "size": 20,
            "totalElements": 3,
            "totalPages": 1,
            "number": 0
          }
        }

    *   **finished** - признак открытытого или завершенного Опроса
    *   **current** - признак текущего Опроса     
    
*   **POST** - Создание нового запроса. Доступно только администраторам

    POST [http://localhost:8080/api/polls]()
    
    *Request body:*

        {
          "date": "2017-02-06",
          "menus": [
            "http://localhost:8080/api/menus/32",
            "http://localhost:8080/api/menus/42",
            "http://localhost:8080/api/menus/52"
            ]
        }

    *   "**http://localhost:8080/api/menus/{id}**" - ссылка на Меню, включаемое в Опрос (см. ниже).         
    
*   **PUT** - Редактирование опроса. Доступно только администраторам
    
    PUT [http://localhost:8080/api/polls/2017-02-06]()
        
    *Request body:*

        {
          "menus": [
            "http://localhost:8080/api/menus/2",
            "http://localhost:8080/api/menus/12",
            "http://localhost:8080/api/menus/22"
            ]
        }


#### /api/menus

#### /api/restaurants

#### /api/users

### Профили приложения

Реализована поддержка 4-х профилей: dev, demo, prod и test:

- dev:

- demo:

- prod: 

- test:

### Запуск приложения

Для запуска приложения требуется установленные [Java](https://java.com), [Git](https://git-scm.com/) и [Maven](https://maven.apache.org/).
В командной строке выполнить команды: 

    git clone https://github.com/Cepr0/restvotes.git
    cd restvotes
    mvn spring-boot:run

## Демо приложения на Heroku 

[Demo](https://restvotes.herokuapp.com/api)

Для удобства работы с приложением в браузере, в приложение добавлен [The HAL Browser](http://docs.spring.io/spring-data/rest/docs/current/reference/html/#_the_hal_browser).
Полный фунционал приложения лучше тестировать в приложении [Postman](https://www.getpostman.com/). 