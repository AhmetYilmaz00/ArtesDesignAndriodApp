package com.artesdesign.data

sealed class Category(val category: String){

    object Chair:Category("Sandalye")
    object Cupboard:Category("Dolap")
    object Table:Category("Masa")
    object Sofa:Category("Kanepe")
    object Bed:Category("Yatak")

}
