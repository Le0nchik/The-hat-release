package com.example.the_hat.backendClasses.dicts

class Dict<E> {
    var unused: RandomList<E>
    val used: MutableList<E> = ArrayList()

    constructor(unused: MutableList<E>) {
        this.unused = RandomList(unused)
    }

    constructor(unused: RandomList<E>) {
        this.unused = unused
    }

    fun get(): E {
        val el = unused.remove()
        used.add(el)
        return el
    }

    override fun toString(): String {
        return "Dict{" +
                "unused=" + unused +
                ", used=" + used +
                '}'
    }
}
