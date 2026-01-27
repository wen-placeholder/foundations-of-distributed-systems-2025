import phe as paillier

class Client:
    """
    Client class for Paillier-based shopping cart system.
    Handles key generation, cart encryption, and total decryption.
    """
    
    def __init__(self):
        self.public_key = None
        self.private_key = None
    
    def generate_paillier_keypair(self, key_size=2048):
        """
        Generate Paillier public and private keys.
        
        :param key_size: Size of the key in bits (default: 2048)
        :return: Tuple (public_key, private_key)
        """
        self.public_key, self.private_key = paillier.generate_paillier_keypair(n_length=key_size)
        return self.public_key, self.private_key
    
    def encrypt_cart(self, cart):
        """
        Encrypt the quantities in the shopping cart.
        
        :param cart: List of tuples (price, quantity) where price is plaintext
        :return: List of tuples (price, encrypted_quantity)
        """
        if self.public_key is None:
            raise ValueError("Keys must be generated before encrypting cart.")
        
        encrypted_cart = []
        for price, quantity in cart:
            # Encrypt only the quantity, keep price in plaintext
            encrypted_quantity = self.public_key.encrypt(quantity)
            encrypted_cart.append((price, encrypted_quantity))
        
        return encrypted_cart
    
    def decrypt_total(self, encrypted_total):
        """
        Decrypt the total cost received from the server.
        
        :param encrypted_total: Encrypted total cost
        :return: Decrypted total cost (integer)
        """
        if self.private_key is None:
            raise ValueError("Private key is required for decryption.")
        
        return self.private_key.decrypt(encrypted_total)


class Server:
    """
    Server class for computing encrypted total cost.
    Works with encrypted quantities without seeing the actual values.
    """
    
    @staticmethod
    def compute_encrypted_total(encrypted_cart):
        """
        Compute the total cost of the shopping cart using encrypted quantities.
        
        :param encrypted_cart: List of tuples (price, encrypted_quantity)
        :return: Encrypted total cost
        """
        encrypted_total = None
        
        for price, encrypted_quantity in encrypted_cart:
            # Compute: price * encrypted_quantity
            # This uses Paillier's homomorphic property: scalar multiplication
            encrypted_item_cost = price * encrypted_quantity
            
            # Add to running total
            # This uses Paillier's additive homomorphic property
            if encrypted_total is None:
                encrypted_total = encrypted_item_cost
            else:
                encrypted_total = encrypted_total + encrypted_item_cost
        
        return encrypted_total


# --- DEMONSTRATION ---

def main():
    print("="*70)
    print("       Paillier Homomorphic Encryption - Shopping Cart Demo")
    print("="*70 + "\n")
    
    # Step 1: Client generates keypair
    print("Step 1: Client generates Paillier keypair")
    client = Client()
    pub_key, priv_key = client.generate_paillier_keypair(key_size=2048)
    print(f"  ✓ Keys generated (key size: 2048 bits)\n")
    
    # Step 2: Client creates and encrypts shopping cart
    print("Step 2: Client populates shopping cart")
    cart = [
        (2000, 1),   # Item 1: price=2000, quantity=1
        (120, 5),    # Item 2: price=120, quantity=5
        (1999, 3),   # Item 3: price=1999, quantity=3
    ]
    
    print("  Shopping cart (plaintext):")
    for i, (price, qty) in enumerate(cart, 1):
        print(f"    Item {i}: price={price}, quantity={qty}")
    
    # Calculate expected total for verification
    expected_total = sum(price * qty for price, qty in cart)
    print(f"\n  Expected total: {expected_total}\n")
    
    print("Step 3: Client encrypts cart quantities")
    encrypted_cart = client.encrypt_cart(cart)
    print("  ✓ Quantities encrypted (prices remain in plaintext)\n")
    
    # Step 4: Server computes encrypted total
    print("Step 4: Server computes encrypted total cost")
    server = Server()
    encrypted_total = server.compute_encrypted_total(encrypted_cart)
    print("  ✓ Server computed encrypted total (without seeing quantities)\n")
    
    # Step 5: Client decrypts the total
    print("Step 5: Client decrypts the total cost")
    decrypted_total = client.decrypt_total(encrypted_total)
    print(f"  ✓ Decrypted total: {decrypted_total}\n")
    
    # Verification
    print("VERIFICATION:")
    print(f"  Expected total:  {expected_total}")
    print(f"  Decrypted total: {decrypted_total}")
    print(f"  Match: {'✓ YES' if expected_total == decrypted_total else '✗ NO'}")
    
    # Additional demonstration: Show that server never sees actual quantities
    print("="*70)
    print("PRIVACY DEMONSTRATION:")
    print("="*70)
    print("What the server sees:")
    print("  - Prices: [2000, 120, 1999] (plaintext)")
    print("  - Quantities: [EncryptedNumber(...), EncryptedNumber(...), EncryptedNumber(...)]")
    print("\nWhat the server does NOT see:")
    print("  - Actual quantity values: [1, 5, 3]")
    print("  - Individual item costs: [2000, 600, 5997]")
    print("\nThe server can compute the total cost without learning:")
    print("  ✓ How many of each item was purchased")
    print("  ✓ The cost of individual items in the cart")


if __name__ == "__main__":
    main()