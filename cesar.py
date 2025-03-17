def cesar_dechiffrement(texte, decalage):
    """Déchiffre un texte avec le chiffrement de César en décalant les lettres."""
    resultat = ""
    for caractere in texte:
        if caractere.isalpha():
            majuscule = caractere.isupper()
            #base = ord('A') if majuscule else ord('a')
            resultat += chr((ord(caractere) - decalage))
        else:
            resultat += caractere
    return resultat

def vigenere_dechiffrement(texte, cle):
    """Déchiffre un texte avec le chiffrement de Vigenère en utilisant une clé."""
    resultat = ""
    cle = cle.lower()
    cle_index = 0
    for caractere in texte:
        if caractere.isalpha():
            majuscule = caractere.isupper()
            base = ord('A') if majuscule else ord('a')
            decalage = ord(cle[cle_index % len(cle)]) - ord('a')
            resultat += chr((ord(caractere) - base - decalage) % 26 + base)
            cle_index += 1
        else:
            resultat += caractere
    return resultat

# Données fournies
#texte_chiffre = "L|k€y+*^*zo‚*€kvsno|*k€om*vo*zk}}*cyvksr" #input("Entrez le texte chiffré par Vigenère : ")
#cle_chiffree = "Tu quoque fili" #input("Entrez la clé de déchiffrement : ")

# Brute force sur la clé chiffrée
#print("\n🔍 Tentatives de déchiffrement :")
#for decalage in range(26):
    #cle_possible = cesar_dechiffrement(cle_chiffree, decalage)
    #texte_dechiffre = vigenere_dechiffrement(texte_chiffre, cle_possible)
    #print(f"Décalage {decalage} | Clé : {cle_possible} -> Texte : {texte_dechiffre}")

# Exemple d'utilisation


#texte_dechiffre = vigenere_dechiffrement(texte, cle)
#rint(f"Texte déchiffré : {texte_dechiffre}")

texte = "PTLJAM(jLz4y_1Z_aO@a_fVB?)"#"L|k€y+*^*zo‚*€kvsno|*k€om*vo*zk}}*cyvksr"#"Lkyzokvsnokomvozkcyvksr"
for i in range(26):
    print("Decalage",i," -> Texte : ",cesar_dechiffrement(texte, i))