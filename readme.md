# RESTVotes
### Voting System for Restaurant lunches
##### Тестовый проект
[![Build Status](https://travis-ci.org/Cepr0/restvotes.svg?branch=master)](https://travis-ci.org/Cepr0/restvotes)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6893de10620c4c779d5659949b55bb82)](https://www.codacy.com/app/Cepr0/restvotes)
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
    - If it is before 11:00 we assume that he changed his mind.
    - If it is after 11:00 then it is too late, vote can't be changed

Each restaurant provides new menu each day.

As a result, provide a link to github repository. It should contain the code, README.md with API documentation and couple curl commands to test it.

P.S.: Make sure everything works with latest version that is on github :)

P.P.S.: Assume that your API will be used by a frontend developer to build frontend on top of that.

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

Применение [Spring Data REST](http://projects.spring.io/spring-data-rest/) позволило реализовать REST API с использованием технологии [HATEOAS](http://spring-projects.ru/understanding/hateoas/) - Hypermedia as the Engine of Application State.  

### Структура данных

![Структура данных][data-structure]
[data-structure]: data-structure.png "Структура данных"

#### Poll - Опрос
Центральный объект приложения - описывает голосование за лучшее меню/ресторан в течение одного дня. 
Содержит ссылку на **список** объектов типа Меню (см. ниже), которые учавствуют в голосовании, и ссылку на Меню-победителя (для завершенного опроса). 

В течение дня может существовать только один Опрос.
Опрос не может содержать несколько Меню одного и того же Ресторана.
Нельзя создать Опрос за прошедший день или за текущий день, если голосование уже завершилось (по умолчанию - 11:00).
Нельзя удалить или изменить опрос, в котором пользователи уже голосовали.

Голосовать можно только в рамках текущего незавершенного Опроса.
Опрос открыт до установленного времени (по умолчанию - до 11:00).
Текущим опросом считается первый незавершенный, либо последний завершенный Опрос. 
  
#### Menu - Меню
Содержит ссылку на объект Ресторан (см. ниже), к которому относиться данное Меню, и на **список** объектов MenuItem (пункт меню, см. ниже).

Одно и тоже меню может входить в разные опросы. 
Если за какое-либо меню голосовали, то такое меню не подлежит корректировке. 

#### MenuItem - Пункт меню
Описывает блюдо и его цену.

Блюда существуют только в пределах Меню, в которое они входят. 

#### Restaurant - Ресторан
Описывает Ресторан (название, адрес и т.п.).

Нельзя создать два ресторана с одним и тем же названием.

#### User - Пользователь
Описывает пользователя данного сервиса. Может быть обычным пользователем или администратором (тот же пользователь, но с расширенными правами).

Нельзя зарегистрировать двух пользователей с одним и тем же адресом электронной почты.

Администраторы могут создавать/редактировать: Опросы, Меню, Блюда, Рестораны др. пользователей, и выполнять действия доступные обычным Пользователям.

Пользователи могут: регистрироваться на сервисе, редактировать свой профиль и голосовать за выбранное Меню/Ресторан в течение открытого Опроса.

#### Vote - Голос
Предназначен для регистрации голоса Пользователя, который он отдал за выбранное Меню/Ресторан в течение текущего незавершенного Опроса.

Голосовать можно только в течение незавершенного текущего Опроса.
Если Пользователь голосует второй раз в течение открытого Опроса, то его предыдущий голос перезаписывается.

### Настройки приложения

#### application.properties
- **restvotes.new_day_poll_time**=9:00 - время начала опроса в текущем (новом) дне. 
Если до этого момента Администратор не создал опрос, приложение сделает это автоматически на основе предыдущего 
завершенного запроса - скопирует его список Меню. 
      
- **restvotes.end_of_voting_time**=11:00 - время завершения опроса. 
Программа автоматически завершает опрос и определяет победителя. 

### Поведение

#### Старт приложения
1.  Программа проверяет: если сейчас время после окончания Опроса, то закрывает все незавершенные Опросы на текущий момент. Иначе - завершает все Опросы до начала сегодняшнего дня.  
2.  Проверяет во всех ли закрытых Опросах проставлены победители. Проставляет, если не проставлены.  
3.  Ищет "пустые" Опросы, которые не содержат голосов Пользователей, и удаляет найденные опросы. 
4.  Если сейчас время после "начала опроса", но до "завершения опроса", программа автоматически создает новый Опрос (если Опрос не был сделан ранее Администратором) - копирует список меню последнего завершенного Опроса (предполагается, что сегодня может быть такое же Меню как и вчера). До времени "начала голосования" Администратор может создать Опрос вручную.

#### Начало нового дня (0:00:01)
1.  Программа проверяет: проставлены ли победители в завершенных Опросах, если нет - проставляет.   
2.  Удаляет "пустые" Опросы 

#### Начало голосования (по умолчанию - 9:00)
1.  Если Опрос еще не создан за сегодня, программа автоматически создает новый Опрос - копирует список Меню последнего закрытого Опроса. 

#### Завершение голосования (по умолчанию - 11:00)
1.  Завершает все незавершенные Опросы на текущий момент.
2.  Проставляет победителя в завершенных Опросах.

### Описание интерфейса (API)

#### /api

Выводит список доступных объектов: Опросы, Меню, Рестораны, Пользователи

Команды:
*   **GET** - Получение списка объектов приложения. Доступна всем посетителям.

    GET [http://localhost:8080/api](http://localhost:8080/api)
 
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

Работа с профилем обычного пользователя.

Команды:
*   **GET** - Получение профиля. Доступна авторизованному пользователю.

    GET [http://localhost:8080/api/userProfile](http://localhost:8080/api/userProfile)

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

*   **POST** - Регистрация нового пользователя. Доступна всем посетителям
    
    POST [http://localhost:8080/api/userProfile](http://localhost:8080/api/userProfile)

    *Request body:*

        {
          "name": "Givi Zurabovich",
          "password": "123456",
          "email": "givi@restvotes.com",
        }

*   **PUT** - Редактирование профиля. Доступна авторизованному пользователю (тело запроса аналогично команде POST).
    
    PUT [http://localhost:8080/api/userProfile](http://localhost:8080/api/userProfile)

#### /api/polls

Работа с Опросами.

Команды:
*   **GET** - Просмотр всех опросов. Доступна авторизованному пользователю.

    GET [http://localhost:8080/api/polls](http://localhost:8080/api/polls)
        
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

    *   **finished** - признак завершенного Опроса
    *   **current** - признак текущего Опроса     
    
*   **GET** - Получение текущего Опроса. Доступна авторизованному пользователю.
    
    POST [http://localhost:8080/api/polls/current](http://localhost:8080/api/polls/current)
    
        {
          "date": "2017-02-06",
          "finished": false,
          "current": true,
          "_embedded": {
            "menus": [
              {
                "chosen": true,
                "rank": 3,
                "winner": false,
                "restaurant": {
                  "name": "Rest1",
                  "address": "Address1",
                  "url": "http://rest1.com",
                  "phone": "1234567890"
                },
                "items": [
                  {
                    "description": "Description1 M4",
                    "cost": 16
                  },
                  {
                    "description": "Description2 M4",
                    "cost": 21
                  },
                  {
                    "description": "Description3 M4",
                    "cost": 11
                  }
                ],
                "price": 48,
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/menus/32"
                  },
                  "restaurant": {
                    "href": "http://localhost:8080/api/restaurants/2",
                    "title": "Ресторан"
                  },
                  "vote": {
                    "href": "http://localhost:8080/api/menus/32/vote",
                    "title": "Голосовать"
                  }
                }
              },
              {
                "chosen": false,
                "rank": 3,
                "winner": false,
                "restaurant": {
                  "name": "Rest2",
                  "address": "Address2",
                  "url": "http://rest2.com",
                  "phone": "2345678901"
                },
                "items": [
                  {
                    "description": "Description1 M5",
                    "cost": 16.5
                  },
                  {
                    "description": "Description2 M5",
                    "cost": 21.5
                  },
                  {
                    "description": "Description3 M5",
                    "cost": 11.5
                  }
                ],
                "price": 49.5,
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/menus/42"
                  },
                  "restaurant": {
                    "href": "http://localhost:8080/api/restaurants/12",
                    "title": "Ресторан"
                  },
                  "vote": {
                    "href": "http://localhost:8080/api/menus/42/vote",
                    "title": "Голосовать"
                  }
                }
              },
              {
                "chosen": false,
                "rank": 2,
                "winner": false,
                "restaurant": {
                  "name": "Rest3",
                  "address": "Address3",
                  "url": "http://rest3.com",
                  "phone": "3456789012"
                },
                "items": [
                  {
                    "description": "Description1 M6",
                    "cost": 16.9
                  },
                  {
                    "description": "Description2 M6",
                    "cost": 21.9
                  },
                  {
                    "description": "Description3 M6",
                    "cost": 11.9
                  }
                ],
                "price": 50.7,
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/menus/52"
                  },
                  "restaurant": {
                    "href": "http://localhost:8080/api/restaurants/22",
                    "title": "Ресторан"
                  },
                  "vote": {
                    "href": "http://localhost:8080/api/menus/52/vote",
                    "title": "Голосовать"
                  }
                }
              }
            ]
          },
          "_links": {
            "self": {
              "href": "http://localhost:8080/api/polls/2017-02-06"
            },
            "userChoice": {
              "href": "http://localhost:8080/api/menus/32",
              "title": "Текущий выбор пользователя"
            }
          }
        }    

    *   **chosen** - меню, за которое проголосовал текущий пользователь
    *   **rank** - кол-во проголосовавших за данное меню
    *   **winner** - признак победившего Меню (для завершившегося Опроса)
    *   **vote** - ссылка для голосования за данное меню (команда PUT - см. ниже)
    *   **userChoice** - ссылка на Меню - текущий выбор пользователя     

*   **POST** - Создание нового запроса. Доступна только администраторам

    POST [http://localhost:8080/api/polls](http://localhost:8080/api/polls)
    
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
    
*   **PUT** - Редактирование опроса. Доступна только администраторам
    
    PUT [http://localhost:8080/api/polls/2017-02-06](http://localhost:8080/api/polls/2017-02-06)
        
    *Request body:*

        {
          "menus": [
            "http://localhost:8080/api/menus/2",
            "http://localhost:8080/api/menus/12",
            "http://localhost:8080/api/menus/22"
            ]
        }

    *   "**http://localhost:8080/api/menus/{id}**" - ссылка на Меню, включаемое в Опрос (см. ниже).         

#### /api/menus

Работа со списком Меню.

Команды:

* **GET** - Получение списка всех Меню. Доступна авторизованным пользователям.
    
    GET [http://localhost:8080/api/menus?size=3](http://localhost:8080/api/menus?size=3)
    
    *Response body:*
    
        {
          "_embedded": {
            "menus": [
              {
                "items": [
                  {
                    "description": "Description1 M1",
                    "cost": 15
                  },
                  {
                    "description": "Description2 M1",
                    "cost": 20
                  },
                  {
                    "description": "Description3 M1",
                    "cost": 10
                  }
                ],
                "price": 45,
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/menus/1"
                  },
                  "menu": {
                    "href": "http://localhost:8080/api/menus/1{?projection}",
                    "templated": true,
                    "title": "Меню"
                  },
                  "restaurant": {
                    "href": "http://localhost:8080/api/menus/1/restaurant",
                    "title": "Ресторан"
                  }
                }
              },
              {
                "items": [
                  {
                    "description": "Description1 M2",
                    "cost": 15.5
                  },
                  {
                    "description": "Description2 M2",
                    "cost": 20.5
                  },
                  {
                    "description": "Description3 M2",
                    "cost": 10.5
                  }
                ],
                "price": 46.5,
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/menus/2"
                  },
                  "menu": {
                    "href": "http://localhost:8080/api/menus/2{?projection}",
                    "templated": true,
                    "title": "Меню"
                  },
                  "restaurant": {
                    "href": "http://localhost:8080/api/menus/2/restaurant",
                    "title": "Ресторан"
                  }
                }
              },
              {
                "items": [
                  {
                    "description": "Description1 M3",
                    "cost": 15.9
                  },
                  {
                    "description": "Description2 M3",
                    "cost": 20.9
                  },
                  {
                    "description": "Description3 M3",
                    "cost": 10.9
                  }
                ],
                "price": 47.7,
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/menus/3"
                  },
                  "menu": {
                    "href": "http://localhost:8080/api/menus/3{?projection}",
                    "templated": true,
                    "title": "Меню"
                  },
                  "restaurant": {
                    "href": "http://localhost:8080/api/menus/3/restaurant",
                    "title": "Ресторан"
                  }
                }
              }
            ]
          },
          "_links": {
            "first": {
              "href": "http://localhost:8080/api/menus?page=0&size=3"
            },
            "self": {
              "href": "http://localhost:8080/api/menus"
            },
            "next": {
              "href": "http://localhost:8080/api/menus?page=1&size=3"
            },
            "last": {
              "href": "http://localhost:8080/api/menus?page=1&size=3"
            },
            "profile": {
              "href": "http://localhost:8080/api/profile/menus"
            }
          },
          "page": {
            "size": 3,
            "totalElements": 6,
            "totalPages": 2,
            "number": 0
          }
        }

    * **?page**=3 - дополнительный параметр запроса, задает кол-во элементов на странице выдачи результата.

* **POST** - Создание нового Меню. Доступна администраторам.
    
    POST [http://localhost:8080/api/menus](http://localhost:8080/api/menus)
    
    *Request body:*
    
        {
            "items": [
                {
                    "description": "Description1",
                    "cost": 15
                },
                {
                    "description": "Description2",
                    "cost": 20
                },
                {
                    "description": "Description3",
                    "cost": 10
                }
                ],
                "restaurant": "http://localhost:8080/api/restaurants/1"
        }
    
    *  **"http://localhost:8080/api/restaurants/1"** - ссылка на ресторан, к которому принадлежит создаваемое меню.
    
* **PUT** - Редактирование Меню. Доступна администраторам.
    
    POST [http://localhost:8080/api/menus/1](http://localhost:8080/api/menus/1)
    
    *Request body:*
    
        {
            "items": [
                {
                    "description": "Description1",
                    "cost": 15.5
                },
                {
                    "description": "Description2",
                    "cost": 20.5
                },
                {
                    "description": "Description3",
                    "cost": 10.5
                }
                ],
                "restaurant": "http://localhost:8080/api/restaurants/2"
        }
    
    *  **"http://localhost:8080/api/restaurants/1"** - ссылка на ресторан, к которому принадлежит создаваемое меню.
    
    Изменять меню можно только если оно еще не участвовало в опросах.
    
* **DELETE** - Удаление Меню. Доступна администраторам.  
    
    DELETE [http://localhost:8080/api/menus/1](http://localhost:8080/api/menus/1)

    Удалять меню можно только если оно еще не участвовало в опросах.  

#### /api/restaurants

Работа со списком Ресторанов.

Команды:

* **GET** - Получение списка всех Ресторанов. Доступна авторизованным пользователям.
    
    GET [http://localhost:8080/api/restaurants](http://localhost:8080/api/restaurants)
    
    *Response body:*
    
        {
          "_embedded": {
            "restaurants": [
              {
                "name": "Rest1",
                "address": "Address1",
                "url": "http://rest1.com",
                "phone": "1234567890",
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/restaurants/1"
                  },
                  "restaurant": {
                    "href": "http://localhost:8080/api/restaurants/1",
                    "title": "Ресторан"
                  },
                  "menus": {
                    "href": "http://localhost:8080/api/restaurants/1/menus",
                    "title": "Список меню"
                  }
                }
              },
              {
                "name": "Rest2",
                "address": "Address2",
                "url": "http://rest2.com",
                "phone": "2345678901",
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/restaurants/2"
                  },
                  "restaurant": {
                    "href": "http://localhost:8080/api/restaurants/2",
                    "title": "Ресторан"
                  },
                  "menus": {
                    "href": "http://localhost:8080/api/restaurants/2/menus",
                    "title": "Список меню"
                  }
                }
              },
              {
                "name": "Rest3",
                "address": "Address3",
                "url": "http://rest3.com",
                "phone": "3456789012",
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/restaurants/3"
                  },
                  "restaurant": {
                    "href": "http://localhost:8080/api/restaurants/3",
                    "title": "Ресторан"
                  },
                  "menus": {
                    "href": "http://localhost:8080/api/restaurants/3/menus",
                    "title": "Список меню"
                  }
                }
              }
            ]
          },
          "_links": {
            "self": {
              "href": "http://localhost:8080/api/restaurants"
            },
            "profile": {
              "href": "http://localhost:8080/api/profile/restaurants"
            },
            "search": {
              "href": "http://localhost:8080/api/restaurants/search",
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

    * **"http://localhost:8080/api/restaurants/{id}/menus"** - список всех меню выбранного ресторана (см. ниже);
    * **"http://localhost:8080/api/restaurants/search"** - поиск по списку ресторанов (см. ниже).
    
* **POST** - Создание нового Ресторана. Доступна администраторам.
    
    POST [http://localhost:8080/api/restaurants](http://localhost:8080/api/restaurants)
    
    *Request body:*
    
        {
          "name": "Rest1",
          "address": "Address1",
          "url": "http://rest1.com",
          "phone": "1234567890"
        }    

    * **name** - уникальное поле.
    
* **PUT** - Редактирование Ресторана. Доступна администраторам.
    
    POST [http://localhost:8080/api/restaurants/1](http://localhost:8080/api/restaurants/1)
    
    *Request body:*
    
        {
          "name": "Rest1",
          "address": "Address1",
          "url": "http://rest1.com",
          "phone": "0987654321"
        }    

* **DELETE** - Удаление Ресторана. Доступна администраторам. Удалить можно только не используемый ранее Ресторан.
    
    DELETE [http://localhost:8080/api/restaurants/1](http://localhost:8080/api/restaurants/1)

* **GET** - Получить список всех меню, связанных с выбранным рестораном. Доступна авторизованным пользователям.

    GET [http://localhost:8080/api/restaurants/1/menus](http://localhost:8080/api/restaurants/1/menus)

* **GET** - Поиск по списку ресторанов. Доступна авторизованным пользователям.

    GET [http://localhost:8080/api/restaurants/search](http://localhost:8080/api/restaurants/search)
    
    *Response:*
    
        {
          "_links": {
            "byAddress": {
              "href": "http://localhost:8080/api/restaurants/search/byAddress{?address}",
              "templated": true
            },
            "byName": {
              "href": "http://localhost:8080/api/restaurants/search/byName{?name}",
              "templated": true
            },
            "self": {
              "href": "http://localhost:8080/api/restaurants/search"
            }
          }
        }    

    * **http://localhost:8080/api/restaurants/search/byAddress**?address=Address - поиск по адресу;
    * **http://localhost:8080/api/restaurants/search/byName**?name=Rest - поиск по наименованию.

#### /api/users

Работа с пользователями. Доступ только для Администраторов.

Команды:

* **GET** - Получение списка всех Пользователей. 
    
    GET [http://localhost:8080/api/users](http://localhost:8080/api/users)
    
    *Response:*

        {
          "_embedded": {
            "users": [
              {
                "name": "Frodo Baggins",
                "email": "frodo@restvotes.com",
                "enabled": true,
                "role": "ROLE_ADMIN",
                "registered": "2017-02-12 13:32:18",
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/users/1"
                  },
                  "user": {
                    "href": "http://localhost:8080/api/users/1",
                    "title": "Пользователь"
                  }
                }
              },
              {
                "name": "Gandalf the Grey",
                "email": "gandalf@restvotes.com",
                "enabled": true,
                "role": "ROLE_ADMIN",
                "registered": "2017-02-12 13:32:18",
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/users/2"
                  },
                  "user": {
                    "href": "http://localhost:8080/api/users/2",
                    "title": "Пользователь"
                  }
                }
              },
              {
                "name": "Sam Gamgee",
                "email": "sam@restvotes.com",
                "enabled": true,
                "role": "ROLE_USER",
                "registered": "2017-02-12 13:32:18",
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/users/3"
                  },
                  "user": {
                    "href": "http://localhost:8080/api/users/3",
                    "title": "Пользователь"
                  }
                }
              }
            ]
          },
          "_links": {
            "first": {
              "href": "http://localhost:8080/api/users?page=0&size=3"
            },
            "self": {
              "href": "http://localhost:8080/api/users"
            },
            "next": {
              "href": "http://localhost:8080/api/users?page=1&size=3"
            },
            "last": {
              "href": "http://localhost:8080/api/users?page=2&size=3"
            },
            "profile": {
              "href": "http://localhost:8080/api/profile/users"
            },
            "search": {
              "href": "http://localhost:8080/api/users/search",
              "title": "Поиск"
            }
          },
          "page": {
            "size": 3,
            "totalElements": 8,
            "totalPages": 3,
            "number": 0
          }
        }

* **POST** - Создание нового Пользователя.
    
    POST [http://localhost:8080/api/users](http://localhost:8080/api/users)
    
    *Request body:*
    
        {
          "name": "Vasya Pupkin",
          "password": "123456",
          "email": "vasya@restvotes.com",
          "role": "ROLE_ADMIN"
          }

    * **name** - не меньше 3 символов;
    * **email** - уникальное поле;
    * **password** - не меньше 6 символов.
    
    По умолчанию создается активный пользователь с ролью ROLE_USER (поля 'enabled' и 'role' можно не указывать).  
    
* **PUT** - Редактирование пользователя.
    
    PUT [http://localhost:8080/api/users/1](http://localhost:8080/api/users/1)
    
    *Request body:*
    
        {
          "name": "Vasiliy Pupkin",
          "password": "123456",
          "email": "vasya@restvotes.com",
          "role": "ROLE_USER",
          "enabled": false
          }

* **DELETE** - Удаление пользователя. Удалить можно только еще не голосовавшего пользователя.
    
    DELETE [http://localhost:8080/api/users/1](http://localhost:8080/api/users/1)

* **GET** - Поиск по списку пользователей.

    GET [http://localhost:8080/api/users/search](http://localhost:8080/api/users/search)
    
    *Response:*

        {
          "_links": {
            "enabled": {
              "href": "http://localhost:8080/api/users/search/enabled"
            },
            "disabled": {
              "href": "http://localhost:8080/api/users/search/disabled"
            },
            "byRole": {
              "href": "http://localhost:8080/api/users/search/byRole{?role}",
              "templated": true
            },
            "byName": {
              "href": "http://localhost:8080/api/users/search/byName{?name}",
              "templated": true
            },
            "byEmail": {
              "href": "http://localhost:8080/api/users/search/byEmail{?email}",
              "templated": true
            },
            "self": {
              "href": "http://localhost:8080/api/users/search"
            }
          }
        }

### Профили приложения

Реализована поддержка 4-х профилей: dev, demo, prod и test:

- **dev** - разработка (профиль по-умолчанию). Используется БД H2 в памяти; заполняется демо данными; настроено логирование запросов в БД и логирование результатов (см. файл 'application-dev.properties').    

- **demo** - предназначен для деплоя на Heroku. Используется БД MySQL; заполняется демо данными; настроено логирование запросов в БД и логирование результатов (см. файл 'application-demo.properties').

- **prod** - предназначен для работы на продуктиве. Используется БД H2 в файле; не заполняется демо данными (см. файл 'application-prod.properties').  

- **test** - предназначен для выполнения тестов. Используется БД H2 в памяти; заполняется демо данными; настроено логирование запросов в БД и логирование результатов (см. файл 'application-test.properties').
 
Для задания нужно профиля, нужно задать параметр _spring.profiles.active=dev_ в 'application.properties' либо аргумет запуска приложения _-Dspring.profiles.active=demo_ (см. файл 'Procfile').

### Запуск приложения

Для запуска приложения требуется установленные [Java](https://java.com), [Git](https://git-scm.com/) и [Maven](https://maven.apache.org/).
В командной строке выполнить команды: 

    git clone https://github.com/Cepr0/restvotes.git
    cd restvotes
    mvn spring-boot:run

## Демо приложения на Heroku 

[Demo](https://restvotes.herokuapp.com/api)

Для удобства работы в браузере, в приложение добавлен [The HAL Browser](http://docs.spring.io/spring-data/rest/docs/current/reference/html/#_the_hal_browser).

Рекомендуется тестировать функционал в приложении [Postman](https://www.getpostman.com/).

Демо пользователи: 
- **ADMIN**: frodo@restvotes.com, пароль: 123456
- **USER**: sam@restvotes.com, пароль: 123456

и др. (см. /api/users)