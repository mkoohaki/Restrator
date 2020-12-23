package com.gc200412272.restrator;

class Comment {

    // declare class properties
    var id: String? = null
    var name: String? = null
    var comment: String? = null
    var time: String? = null
    var restaurantId: String? = null

    // empty constructor
    constructor() {}

    constructor(id: String, name: String, comment: String, time: String, restaurantId: String) {
        this.id = id
        this.name = name
        this.comment = comment
        this.time = time
        this.restaurantId = restaurantId
    }
}
