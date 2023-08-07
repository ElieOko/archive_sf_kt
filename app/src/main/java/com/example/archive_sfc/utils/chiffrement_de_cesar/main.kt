package com.example.archive_sfc.utils.chiffrement_de_cesar

val alpha = arrayListOf<Char>()
fun remplissage(chaine:String): ArrayList<Char>{
    var i = 0
    while (i< chaine.length){
        alpha.add(chaine[i])
        i++
    }
    return alpha
}
fun chiffreDeCesarCryptageEtDecryptage(key:Int?,mot:String,cryptage:Boolean=false):String{
    val chaine= "abcdefghijklmnopqrstuvwxyz"
    val allLetter = remplissage(chaine)
    var k : Int = 0
    var i :Int = 0
    var fraction :String =""
    while (k< mot.length ){
        while(i < allLetter.size){
            if (allLetter[i] == mot[k]){
                if(cryptage){
                    fraction+= allLetter[i - 3]
                }
                else{
                    fraction+= allLetter[i + 3]
                }

            }
            i++
        }
        i = 0
        k++
    }
    return fraction
}

