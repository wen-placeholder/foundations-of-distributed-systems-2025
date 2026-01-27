import task1_elgamal;

class Client_EG:
    """
    Client class adapted for ElGamal Shopping Cart.
    """
    def __init__(self):
        self.elgamal = task1_elgamal.ElGamal()
        self.public_key = None
        self.private_key = None

    def setup_keys(self, bit_length=256):
        self.public_key, self.private_key = self.elgamal.generate_keys(bit_length)
        return self.public_key

    def encrypt_cart(self, cart):
        """
        Encrypts BOTH price and quantity.
        ElGamal requires two ciphertexts to perform multiplication: E(P) * E(Q).
        
        Args:
            cart: List of (price, quantity) tuples
        Returns:
            encrypted_cart: List of ((c1_p, c2_p), (c1_q, c2_q)) tuples
        """
        if not self.public_key:
            raise ValueError("Keys not generated")

        encrypted_cart = []
        for price, quantity in cart:
            # Encrypt Price
            enc_price = self.elgamal.encrypt(self.public_key, price)
            # Encrypt Quantity
            enc_quantity = self.elgamal.encrypt(self.public_key, quantity)
            
            encrypted_cart.append((enc_price, enc_quantity))
        
        return encrypted_cart

    def decrypt_and_sum(self, encrypted_subtotals):
        """
        Decrypts a list of subtotals and sums them LOCALLY.
        (The server could not sum them because ElGamal is not additively homomorphic).
        """
        total = 0
        print("   [Client Internal Log] Decrypting received subtotals...")
        
        for i, enc_sub in enumerate(encrypted_subtotals):
            # Decrypt the individual item cost (Price * Qty)
            subtotal = self.elgamal.decrypt(self.public_key, self.private_key, enc_sub)
            total += subtotal
            
        return total


class Server_EG:
    """
    Server class adapted for ElGamal.
    """
    def __init__(self):
        self.elgamal = task1_elgamal.ElGamal()

    def compute_encrypted_subtotals(self, encrypted_cart, public_key):
        """
        Computes the cost for EACH item individually.
        
        Operation: E(Price) * E(Quantity) = E(Price * Quantity)
        
        Note: The server CANNOT sum these up to a final total.
        It returns the list of encrypted subtotals.
        """
        encrypted_subtotals = []
        
        for enc_price, enc_quantity in encrypted_cart:
            # Homomorphic Multiplication
            # Result is E(Price * Quantity)
            enc_product = self.elgamal.multiply(public_key, enc_price, enc_quantity)
            encrypted_subtotals.append(enc_product)
            
        return encrypted_subtotals


# ==========================================
# DEMONSTRATION
# ==========================================

def run_elgamal_scenario():
    print("="*70)
    print("      Task 3: ElGamal Homomorphic Encryption - Shopping Cart")
    print("="*70 + "\n")

    # Step 1
    print("Step 1: Client generates ElGamal keypair")
    client = Client_EG()
    pub_key = client.setup_keys(bit_length=256)
    print(f"  ✓ Keys generated (Modulus p size: {pub_key[0].bit_length()} bits)\n")

    # Step 2
    print("Step 2: Client populates shopping cart")
    cart = [
        (100, 2),   # Item 1
        (50, 4),    # Item 2
        (10, 5)     # Item 3
    ]
    print("  Shopping cart (plaintext):")
    for i, (p, q) in enumerate(cart, 1):
        print(f"    Item {i}: Price={p}, Quantity={q}")
    
    expected_total = sum(p*q for p,q in cart)
    print(f"\n  Expected total: {expected_total}\n")

    # Step 3
    print("Step 3: Client encrypts cart (Price AND Quantity)")
    encrypted_cart = client.encrypt_cart(cart)
    print("  ✓ Price encrypted")
    print("  ✓ Quantity encrypted")
    print("  (Both must be encrypted for ElGamal multiplication)\n")

    # Step 4
    print("Step 4: Server computes encrypted subtotals")
    server = Server_EG()
    encrypted_subtotals = server.compute_encrypted_subtotals(encrypted_cart, pub_key)
    print("  ✓ Server calculated E(Price * Quantity) for each item")
    print("  ! Note: Server CANNOT sum these (ElGamal is not additively homomorphic)\n")

    # Step 5
    print("Step 5: Client decrypts and sums locally")
    final_total = client.decrypt_and_sum(encrypted_subtotals)
    print(f"  ✓ Final Calculated Total: {final_total}\n")

    # Verification
    print("="*70)
    print("VERIFICATION:")
    print(f"  Expected total:  {expected_total}")
    print(f"  Decrypted total: {final_total}")
    print(f"  Match: {'✓ YES' if final_total == expected_total else '✗ NO'}")
    
    # Privacy Demo
    print("="*70)
    print("PRIVACY DEMONSTRATION (ElGamal Architecture):")
    print("="*70)
    print("What the server sees:")
    print("  - Prices:     [Encrypted(...), Encrypted(...), Encrypted(...)]")
    print("  - Quantities: [Encrypted(...), Encrypted(...), Encrypted(...)]")
    print("\nWhat the server does NOT see:")
    print(f"  - Actual Prices:     {[p for p,q in cart]}")
    print(f"  - Actual Quantities: {[q for p,q in cart]}")
    print(f"  - Total Sum:         {expected_total}")
    print("\nDifference from Paillier:")
    print("  ✓ In Paillier, Server saw Plaintext Prices.")
    print("  ✓ In ElGamal, Server sees NOTHING (Full Privacy).")
    print("  ! Trade-off: Client has to do more work (Summation).")

if __name__ == "__main__":
    run_elgamal_scenario()