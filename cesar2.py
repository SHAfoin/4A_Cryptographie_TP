# Message chiffré
secret_message = "PTLJAM(jLz4y_1Z_aO@a_fVB?)"#"L|k€y+*^*zo‚*€kvsno|*k€om*vo*zk}}*cyvksr"

# Fonction pour décrypter avec un décalage donné
def safe_caesar_cipher_decrypt(text, shift):
    decrypted = ""
    for char in text:
        try:
            # Décale chaque caractère en arrière dans la table Unicode
            decrypted_char = chr(ord(char) - shift)
            decrypted += decrypted_char
        except ValueError:
            # Si un caractère est hors de la plage Unicode valide, on met un "?"
            decrypted += '?'
    return decrypted

# On essaie tous les décalages possibles de 1 à 25
for shift in range(1, 26):
    decrypted_message = safe_caesar_cipher_decrypt(secret_message, shift)
    print(f"Décalage {shift}: {decrypted_message}")