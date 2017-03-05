[RU](readme.md) | **EN**

# RESTVotes
### Voting System for Restaurant lunches
##### "Graduation" project for Java-courses [JavaRush](https://javarush.ru/) and [Java Online Projects](http://javaops.ru/)

[![Build Status](https://travis-ci.org/Cepr0/restvotes.svg?branch=master)](https://travis-ci.org/Cepr0/restvotes)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6893de10620c4c779d5659949b55bb82)](https://www.codacy.com/app/Cepr0/restvotes)

## Task
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

## Implementation

### Technology stack

*   Spring Boot 
*   Spring Data JPA
*   Spring Data REST
*   Spring Security
*   H2
*   MySQL
*   JUnit
*   Lombok
*   Maven

[Spring Data REST](http://projects.spring.io/spring-data-rest/) allowed to implement the REST API with [HATEOAS](http://spring-projects.ru/understanding/hateoas/) technology.

### Data structure

![Data structure][data-structure]
[data-structure]: data-structure.png "Структура данных"

#### Poll
The central object in the Application - defines the voting for the best Menu/Restaurant during the one day.  
It contains a link to a related **Menu** list that take part in the poll. 

#### Menu
Menu contains a links to a related **Restaurant** and a **Menu item** list.    

#### MenuItem
Describes a dish and its price. 

#### Restaurant
Describes a restaurant (name, address etc.)

#### User
Describes a user of this Application. It can be Administrator or Ordinary user.

#### Vote
Vote is used to register the user's choice.

### Application settings

#### application.properties:
- **restvotes.new_day_poll_time**=9:00 - the start of voting time. If Administrator has not created a new Poll manually  
then Application creates it automatically by copying the previous one.

- **restvotes.end_of_voting_time**=11:00 - the end of voting time. Application automatically finishes Poll and 
determines a winner. 

### Behavior 

#### Application start
1. Checks all Polls and closes unfinished ones     
2. Determines all finished polls without winners and sets winner for such polls.    
3. Finds 'empty' polls that doesn't have votes and deletes them.  
4. If now it's start voting time and if Admin hasn't created poll manually than copies the previous poll.   

#### New day (0:00:01)
1. Determines all finished polls without winners and sets a winner for every such poll.
2. Deletes 'empty' polls 

#### Start voting (by default - 9:00)
1.  If now it's start voting time and if Admin hasn't created poll manually than copies the previous poll. 

#### End voting (by default - 11:00)
1.  Closes unfinished polls
2.  Determines all finished polls without winners and sets winner for such polls.

### API description

#### /api

Displays list of all available objects: Polls, Menus, Restaurants, Users and links to the current Poll 
and to user Profile.

Commands:
*   **GET** - Getting list of objects. Public access.

    GET [http://localhost:8080/api](http://localhost:8080/api)
 
    *Response:*

        {
          "_links": {
            "users": {
              "href": "http://localhost:8080/api/users{?page,size,sort}",
              "templated": true,
              "title": "User list"
            },
            "menus": {
              "href": "http://localhost:8080/api/menus{?page,size,sort,projection}",
              "templated": true,
              "title": "Menu list"
            },
            "restaurants": {
              "href": "http://localhost:8080/api/restaurants{?page,size,sort}",
              "templated": true,
              "title": "Restaurant list"
            },
            "polls": {
              "href": "http://localhost:8080/api/polls{?page,size,sort}",
              "templated": true,
              "title": "Poll list"
            },
            "currentPoll": {
              "href": "http://localhost:8080/api/polls/current",
              "title": "Current Poll"
            },
            "userProfile": {
              "href": "http://localhost:8080/api/userProfile",
              "title": "User profile"
            },
            "profile": {
              "href": "http://localhost:8080/api/profile"
            }
          }
        }

#### /api/userProfile

Profile for ordinary user.

Commands:
*   **GET** - Getting a profile. Access is available to all authorized users.

    GET [http://localhost:8080/api/userProfile](http://localhost:8080/api/userProfile)

    *Response:*

        {
          "name": "Frodo Baggins",
          "password": "",
          "email": "frodo@restvotes.com",
          "_links": {
            "userProfile": {
              "href": "https://localhost:8080/api/userProfile",
              "title": "User profile"
            }
          }
        }

*   **POST** - User signing up. Access is available to all users.
    
    POST [http://localhost:8080/api/userProfile](http://localhost:8080/api/userProfile)

    *Request body:*

        {
          "name": "Givi Zurabovich",
          "password": "123456",
          "email": "givi@restvotes.com",
        }

*   **PUT** - Updating profile. Access is available to all authorized users.
    
    PUT [http://localhost:8080/api/userProfile](http://localhost:8080/api/userProfile)

#### /api/polls

Working with Polls.

Commands:
*   **GET** - Getting all polls. Access is available to all authorized users.

    GET [http://localhost:8080/api/polls](http://localhost:8080/api/polls)
        
    *Response:*
        
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
                    "title": "Winner"
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
                    "title": "Winner"
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
              "title": "Current poll"
            },
            "profile": {
              "href": "https://localhost:8080/api/profile/polls"
            },
            "search": {
              "href": "https://localhost:8080/api/polls/search",
              "title": "Search"
            }
          },
          "page": {
            "size": 20,
            "totalElements": 3,
            "totalPages": 1,
            "number": 0
          }
        }

    *   **finished** - finished Poll mark
    *   **current** - current poll mark     
    
*   **GET** - Getting the current Poll. Access is available to all authorized users.
    
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
                    "title": "Restaurant"
                  },
                  "vote": {
                    "href": "http://localhost:8080/api/menus/32/vote",
                    "title": "Cast a vote"
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
                    "title": "Restaurant"
                  },
                  "vote": {
                    "href": "http://localhost:8080/api/menus/42/vote",
                    "title": "Cast a vote"
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
                    "title": "Restaurant"
                  },
                  "vote": {
                    "href": "http://localhost:8080/api/menus/52/vote",
                    "title": "Cast a vote"
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
              "title": "User's current choice"
            }
          }
        }    

    *   **chosen** - mark of user's current choice
    *   **rank** - voices count for this Menu 
    *   **winner** - a winner Menu mark (only for finished Poll) 
    *   **vote** - a link to Cast a vote (PUT request)
    *   **userChoice** - a link to user's current choice     

*   **POST** - Creating a new Poll. Access is available to Admin only.

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

    *   "**http://localhost:8080/api/menus/{id}**" - a link to Menu included to Poll.         
    
*   **PUT** - Updating Poll. Access is available to Admins only.
    
    PUT [http://localhost:8080/api/polls/2017-02-06](http://localhost:8080/api/polls/2017-02-06)
        
    *Request body:*

        {
          "menus": [
            "http://localhost:8080/api/menus/2",
            "http://localhost:8080/api/menus/12",
            "http://localhost:8080/api/menus/22"
            ]
        }

    *   "**http://localhost:8080/api/menus/{id}**" - a link to Menu included to Poll.         

#### /api/menus

Working with Menu.

Commands:

* **GET** - Getting a Menu list. Access is available to all authorized users.
    
    GET [http://localhost:8080/api/menus?size=3](http://localhost:8080/api/menus?size=3)
    
    *Response:*
    
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
                    "title": "Menu"
                  },
                  "restaurant": {
                    "href": "http://localhost:8080/api/menus/1/restaurant",
                    "title": "Restaurant"
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
                    "title": "Menu"
                  },
                  "restaurant": {
                    "href": "http://localhost:8080/api/menus/2/restaurant",
                    "title": "Restaurant"
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
                    "title": "Menu"
                  },
                  "restaurant": {
                    "href": "http://localhost:8080/api/menus/3/restaurant",
                    "title": "Restaurant"
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

    * **?page**=3 - an additional request parameter, sets the number of elements on one page.

* **POST** - Creating Menu. Access is available to Admins only.
    
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
    
    *  **"http://localhost:8080/api/restaurants/1"** - a link to Restaurant related to new Menu.
    
* **PUT** - Updating Menu. Access is available to Admins only.
    
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
        
* **DELETE** - Deleting Menu. Access is available to Admins only.  
    
    DELETE [http://localhost:8080/api/menus/1](http://localhost:8080/api/menus/1)

#### /api/restaurants

Working with Restaurants.

Commands:

* **GET** - Getting Restaurant list. Access is available to all authorized users.
    
    GET [http://localhost:8080/api/restaurants](http://localhost:8080/api/restaurants)
    
    *Response:*
    
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
                    "title": "Restaurant"
                  },
                  "menus": {
                    "href": "http://localhost:8080/api/restaurants/1/menus",
                    "title": "Menu list"
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
                    "title": "Resturant"
                  },
                  "menus": {
                    "href": "http://localhost:8080/api/restaurants/2/menus",
                    "title": "Menu list"
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
                    "title": "Restaurant"
                  },
                  "menus": {
                    "href": "http://localhost:8080/api/restaurants/3/menus",
                    "title": "Menu list"
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
              "title": "Search"
            }
          },
          "page": {
            "size": 20,
            "totalElements": 3,
            "totalPages": 1,
            "number": 0
          }
        }

    * **"http://localhost:8080/api/restaurants/{id}/menus"** - Menu list of this Restaurant;
    * **"http://localhost:8080/api/restaurants/search"** - a search commands list.
    
* **POST** - Adding new Restaurant. Access is available to Admins.
    
    POST [http://localhost:8080/api/restaurants](http://localhost:8080/api/restaurants)
    
    *Request body:*
    
        {
          "name": "Rest1",
          "address": "Address1",
          "url": "http://rest1.com",
          "phone": "1234567890"
        }    

    * **name** - is an unique field.
    
* **PUT** - Updating Restaurant. Access is available to Admins.
    
    POST [http://localhost:8080/api/restaurants/1](http://localhost:8080/api/restaurants/1)
    
    *Request body:*
    
        {
          "name": "Rest1",
          "address": "Address1",
          "url": "http://rest1.com",
          "phone": "0987654321"
        }    

* **DELETE** - Deleting restaurants. Access is available to Admins.
    
    DELETE [http://localhost:8080/api/restaurants/1](http://localhost:8080/api/restaurants/1)

* **GET** - Getting Menu list related to this Restaurant. Access is available to all authorized users.

    GET [http://localhost:8080/api/restaurants/1/menus](http://localhost:8080/api/restaurants/1/menus)

* **GET** - Restaurant searching. Access is available to all authorized users.

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

    * **http://localhost:8080/api/restaurants/search/byAddress**?address=Address - find by address;
    * **http://localhost:8080/api/restaurants/search/byName**?name=Rest - find by name.

#### /api/users

Users related commands. Access is available to Admins only.

Commands:

* **GET** - Getting User list. 
    
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
                    "title": "User"
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
                    "title": "User"
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
                    "title": "User"
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
              "title": "Search"
            }
          },
          "page": {
            "size": 3,
            "totalElements": 8,
            "totalPages": 3,
            "number": 0
          }
        }

* **POST** - Creating a new User.
    
    POST [http://localhost:8080/api/users](http://localhost:8080/api/users)
    
    *Request body:*
    
        {
          "name": "Vasya Pupkin",
          "password": "123456",
          "email": "vasya@restvotes.com",
          "role": "ROLE_ADMIN"
        }

    * **name** - at least 3 letters;
    * **email** - an unique field;
    * **password** - at least 6 symbols.
    
    By default the role of new User is ROLE_USER (can be omitted).  
    
* **PUT** - Updating User.
    
    PUT [http://localhost:8080/api/users/1](http://localhost:8080/api/users/1)
    
    *Request body:*
    
        {
          "name": "Vasiliy Pupkin",
          "password": "123456",
          "email": "vasya@restvotes.com",
          "role": "ROLE_USER",
          "enabled": false
        }

* **DELETE** - Deleting User.
    
    DELETE [http://localhost:8080/api/users/1](http://localhost:8080/api/users/1)

* **GET** - User searching.

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

### Application profiles

Application supports 4 profiles: dev, demo, prod и test:

- **dev** - develop (default). Used H2 in memory; demo data propagation; DB requests and responses logging (see 'application-dev.properties').    

- **demo** - for deploy on Heroku. User MySQL; demo data propagation; DB requests and responses logging (see 'application-demo.properties').

- **prod** - production mode. H2 in file (см. файл 'application-prod.properties').  

- **test** - testing. H2 in memory; demo data propagation; DB requests and responses logging (see 'application-test.properties').
 
A necessary profile is setting as the _spring.profiles.active_ parameter of the 'application.properties' or 
as the JVM option _-Dspring.profiles.active=demo_ (see 'Procfile').  

### Launch Application

Application requires: [Java](https://java.com), [Git](https://git-scm.com/) and [Maven](https://maven.apache.org/).
To launch Application type in command line: 

    git clone https://github.com/Cepr0/restvotes.git
    cd restvotes
    mvn spring-boot:run

Than open in browser [http://localhost:8080](http://localhost:8080)

## Application demo on Heroku 

[Demo](https://restvotes.herokuapp.com/api)

For convenient work with the application in the browser, the project has been added [The HAL Browser](http://docs.spring.io/spring-data/rest/docs/current/reference/html/#_the_hal_browser).

But it is recommended to work with Application in [Postman](https://www.getpostman.com/).

Demo users: 
- **ADMIN**: frodo@restvotes.com, password: 123456
- **USER**: sam@restvotes.com, password: 123456

and others (see. /api/users).