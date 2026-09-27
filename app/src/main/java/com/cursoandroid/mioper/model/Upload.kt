package com.cursoandroid.mioper.model

// Getters/setters explícitos para manter os nomes de propriedade que o Firebase já serializa ("nome", "name", "mImageUrl")
class Upload {

    private var mName: String? = null
    private var mImageUrl: String? = null

    constructor()

    constructor(name: String, imageUrl: String?) {
        mName = if (name.trim().isEmpty()) "Nenhum nome" else name
        mImageUrl = imageUrl
    }

    fun getNome(): String? = mName

    fun setName(name: String?) {
        mName = name
    }

    fun getmImageUrl(): String? = mImageUrl

    fun setmImageUrl(imageUrl: String?) {
        mImageUrl = imageUrl
    }
}
