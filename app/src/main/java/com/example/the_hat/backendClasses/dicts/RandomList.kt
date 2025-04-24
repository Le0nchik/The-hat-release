package com.example.the_hat.backendClasses.dicts

class RandomList<E> {
    var list: MutableList<E> = ArrayList()
    var index = 0

    constructor()

    constructor(list: MutableList<E>) {
        this.list = list
        list.shuffle()
    }

    fun get(): E {
        return list[index]
    }

    fun remove(): E {
        val cur = list[index]
        index++
        return cur
    }

    override fun toString(): String {
        return "RandomList{" +
                "list=" + list +
                '}'
    }
}
