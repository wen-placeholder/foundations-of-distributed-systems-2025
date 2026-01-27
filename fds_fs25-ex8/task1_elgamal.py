import random

class ElGamal:
    """
    Implementation of the ElGamal Cryptosystem.
    Supports Key Generation, Encryption, Decryption, and Homomorphic Multiplication.
    """

    @staticmethod
    def _is_prime(n, k=5):
        """
        Helper method: Miller-Rabin primality test to check if a number is prime.
        :param n: Number to test
        :param k: Number of tests (accuracy)
        :return: True if probably prime, False otherwise
        """
        if n <= 1: return False
        if n <= 3: return True
        if n % 2 == 0: return False

        # Find r and d such that n - 1 = 2^r * d
        r, d = 0, n - 1
        while d % 2 == 0:
            r += 1
            d //= 2

        # Run k tests
        for _ in range(k):
            a = random.randint(2, n - 2)
            x = pow(a, d, n)
            if x == 1 or x == n - 1:
                continue
            for _ in range(r - 1):
                x = pow(x, 2, n)
                if x == n - 1:
                    break
            else:
                return False
        return True

    @staticmethod
    def _generate_large_prime(bits=256):
        """
        Helper method: Generates a large prime number of specified bit length.
        """
        while True:
            # Generate a random odd number with 'bits' length
            # Set MSB to ensure full bit length
            n = random.getrandbits(bits)
            n |= (1 << bits - 1) | 1  # Set MSB and LSB to 1
            if ElGamal._is_prime(n):
                return n

    def generate_keys(self, bit_length=256):
        """
        Generates public and private keys.
        """
        # 1. Generate a large prime p
        p = self._generate_large_prime(bit_length)
        
        # 2. Select a generator g
        # In strictly secure implementations, checking the order is required,
        # but factoring p-1 for large bits is computationally infeasible here.
        g = random.randint(2, p - 1)
        
        # 3. Choose private key x
        x = random.randint(2, p - 2)
        
        # 4. Compute public component h
        h = pow(g, x, p)
        
        return (p, g, h), x

    def encrypt(self, public_key, message):
        """
        Encrypts a message using the public key.
        
        :param public_key: Tuple (p, g, h)
        :param message: Integer message to encrypt (must be < p)
        :return: Ciphertext tuple (c1, c2)
        """
        p, g, h = public_key
        
        if message >= p:
            raise ValueError("Message is too large for the modulus p.")

        # 1. Choose a random ephemeral key y
        # 1 < y < p - 1
        y = random.randint(2, p - 2)
        
        # 2. Compute component c1
        # c1 = g^y mod p
        c1 = pow(g, y, p)
        
        # 3. Compute the shared secret s
        # s = h^y mod p
        s = pow(h, y, p)
        
        # 4. Compute component c2 (masked message)
        # c2 = message * s mod p
        c2 = (message * s) % p
        
        return (c1, c2)

    def decrypt(self, public_key, private_key, ciphertext):
        """
        Decrypts a ciphertext using the private key.
        
        :param public_key: Tuple (p, g, h) - needed for modulus p
        :param private_key: Integer x
        :param ciphertext: Tuple (c1, c2)
        :return: Integer message (plaintext)
        """
        p, g, h = public_key
        c1, c2 = ciphertext
        x = private_key
        
        # 1. Compute the shared secret s
        # s = c1^x mod p
        s = pow(c1, x, p)
        
        # 2. Compute the modular inverse of s
        # s_inv = s^(-1) mod p
        # Python's pow(base, -1, mod) computes modular inverse efficiently
        s_inv = pow(s, -1, p)
        
        # 3. Recover the message
        # m = c2 * s_inv mod p
        m = (c2 * s_inv) % p
        
        return m

    def multiply(self, public_key, cipher1, cipher2):
        """
        Performs Homomorphic Multiplication on two ciphertexts.
        E(m1) * E(m2) -> E(m1 * m2)
        
        :param public_key: Tuple (p, g, h)
        :param cipher1: First ciphertext tuple (c1_a, c2_a)
        :param cipher2: Second ciphertext tuple (c1_b, c2_b)
        :return: New ciphertext tuple (new_c1, new_c2) representing m1 * m2
        """
        p, g, h = public_key
        c1_a, c2_a = cipher1
        c1_b, c2_b = cipher2
        
        # Multiply component-wise modulo p
        # new_c1 = c1_a * c1_b mod p
        new_c1 = (c1_a * c1_b) % p
        
        # new_c2 = c2_a * c2_b mod p
        new_c2 = (c2_a * c2_b) % p
        
        return (new_c1, new_c2)


# --- TEST CASES ---

def run_tests():
    print("\n" + "="*70)
    print("          ElGamal Cryptosystem - Comprehensive Test Suite")
    print("="*70 + "\n")
    
    # Setup
    elgamal = ElGamal()
    print("Generating keys (256-bit)...")
    pub_key, priv_key = elgamal.generate_keys(bit_length=256)
    p = pub_key[0]
    print(f"✓ Keys generated successfully")
    print(f"  - Modulus p: {p.bit_length()} bits")
    print(f"  - Generator g: {pub_key[1]}")
    print()

    passed = 0
    total = 10

    # TEST 1: Basic Encryption and Decryption
    print("Test 1: Basic Encryption and Decryption")
    try:
        msg = 12345
        ct = elgamal.encrypt(pub_key, msg)
        decrypted = elgamal.decrypt(pub_key, priv_key, ct)
        assert msg == decrypted
        print(f"  ✓ PASSED - Message: {msg}, Decrypted: {decrypted}")
        passed += 1
    except AssertionError:
        print(f"  ✗ FAILED - Expected {msg}, got {decrypted}")
    print()

    # TEST 2: Homomorphic Multiplication (Small Numbers)
    print("Test 2: Homomorphic Multiplication (Small Numbers)")
    try:
        m1, m2 = 5, 6
        ct1 = elgamal.encrypt(pub_key, m1)
        ct2 = elgamal.encrypt(pub_key, m2)
        ct_prod = elgamal.multiply(pub_key, ct1, ct2)
        dec_prod = elgamal.decrypt(pub_key, priv_key, ct_prod)
        assert dec_prod == m1 * m2
        print(f"  ✓ PASSED - {m1} × {m2} = {dec_prod}")
        passed += 1
    except AssertionError:
        print(f"  ✗ FAILED - Expected {m1*m2}, got {dec_prod}")
    print()

    # TEST 3: Homomorphic Multiplication (Large Random Numbers)
    print("Test 3: Homomorphic Multiplication (Large Random Numbers)")
    try:
        m1 = random.randint(1000, 5000)
        m2 = random.randint(1000, 5000)
        ct1 = elgamal.encrypt(pub_key, m1)
        ct2 = elgamal.encrypt(pub_key, m2)
        ct_prod = elgamal.multiply(pub_key, ct1, ct2)
        dec_prod = elgamal.decrypt(pub_key, priv_key, ct_prod)
        expected = m1 * m2
        assert dec_prod == expected
        print(f"  ✓ PASSED - {m1} × {m2} = {dec_prod}")
        passed += 1
    except AssertionError:
        print(f"  ✗ FAILED - Expected {expected}, got {dec_prod}")
    print()

    # TEST 4: Semantic Security (Probabilistic Encryption)
    print("Test 4: Semantic Security (Probabilistic Encryption)")
    try:
        msg = 999
        ct1 = elgamal.encrypt(pub_key, msg)
        ct2 = elgamal.encrypt(pub_key, msg)
        assert ct1 != ct2  # Ciphertexts must be different
        assert elgamal.decrypt(pub_key, priv_key, ct1) == msg
        assert elgamal.decrypt(pub_key, priv_key, ct2) == msg
        print(f"  ✓ PASSED - Same message produces different ciphertexts")
        print(f"    CT1: {ct1[0] % 10000}...{ct1[1] % 10000}")
        print(f"    CT2: {ct2[0] % 10000}...{ct2[1] % 10000}")
        passed += 1
    except AssertionError:
        print(f"  ✗ FAILED - Ciphertexts are identical or decryption failed")
    print()

    # TEST 5: Multiplication by Identity Element (1)
    print("Test 5: Multiplication by Identity Element (1)")
    try:
        m1 = 456
        ct1 = elgamal.encrypt(pub_key, m1)
        ct_one = elgamal.encrypt(pub_key, 1)
        ct_res = elgamal.multiply(pub_key, ct1, ct_one)
        dec_res = elgamal.decrypt(pub_key, priv_key, ct_res)
        assert dec_res == m1
        print(f"  ✓ PASSED - {m1} × 1 = {dec_res}")
        passed += 1
    except AssertionError:
        print(f"  ✗ FAILED - Expected {m1}, got {dec_res}")
    print()

    # TEST 6: Associativity of Multiplication
    print("Test 6: Associativity of Multiplication")
    try:
        a, b, c = 10, 20, 30
        ct_a = elgamal.encrypt(pub_key, a)
        ct_b = elgamal.encrypt(pub_key, b)
        ct_c = elgamal.encrypt(pub_key, c)
        
        # (A * B) * C
        temp = elgamal.multiply(pub_key, ct_a, ct_b)
        res1 = elgamal.multiply(pub_key, temp, ct_c)
        
        # A * (B * C)
        temp2 = elgamal.multiply(pub_key, ct_b, ct_c)
        res2 = elgamal.multiply(pub_key, ct_a, temp2)
        
        dec1 = elgamal.decrypt(pub_key, priv_key, res1)
        dec2 = elgamal.decrypt(pub_key, priv_key, res2)
        expected = a * b * c
        
        assert dec1 == dec2 == expected
        print(f"  ✓ PASSED - ({a}×{b})×{c} = {a}×({b}×{c}) = {dec1}")
        passed += 1
    except AssertionError:
        print(f"  ✗ FAILED - Left: {dec1}, Right: {dec2}, Expected: {expected}")
    print()

    # TEST 7: Commutativity of Multiplication
    print("Test 7: Commutativity of Multiplication")
    try:
        a, b = 50, 4
        ct_a = elgamal.encrypt(pub_key, a)
        ct_b = elgamal.encrypt(pub_key, b)
        
        res1 = elgamal.multiply(pub_key, ct_a, ct_b) # A * B
        res2 = elgamal.multiply(pub_key, ct_b, ct_a) # B * A
        
        dec1 = elgamal.decrypt(pub_key, priv_key, res1)
        dec2 = elgamal.decrypt(pub_key, priv_key, res2)
        expected = a * b
        
        assert dec1 == dec2 == expected
        print(f"  ✓ PASSED - {a}×{b} = {b}×{a} = {dec1}")
        passed += 1
    except AssertionError:
        print(f"  ✗ FAILED - A×B: {dec1}, B×A: {dec2}, Expected: {expected}")
    print()

    # TEST 8: Max Message Size Boundary
    print("Test 8: Boundary Condition (Maximum Message)")
    try:
        # Message = p - 1 is the largest possible message in Z_p
        max_msg = pub_key[0] - 1
        ct = elgamal.encrypt(pub_key, max_msg)
        dec = elgamal.decrypt(pub_key, priv_key, ct)
        assert dec == max_msg
        print(f"  ✓ PASSED - Max message (p-1) encrypted and decrypted correctly")
        passed += 1
    except AssertionError:
        print(f"  ✗ FAILED - Expected {max_msg}, got {dec}")
    print()

    # TEST 9: Error Handling (Message too large)
    print("Test 9: Error Handling (Message > p)")
    try:
        large_msg = pub_key[0] + 5
        elgamal.encrypt(pub_key, large_msg)
        print(f"  ✗ FAILED - Should have raised ValueError")
    except ValueError as e:
        print(f"  ✓ PASSED - ValueError raised as expected")
        print(f"    Error message: {str(e)}")
        passed += 1
    except Exception as e:
        print(f"  ✗ FAILED - Wrong exception type: {type(e).__name__}")
    print()

    # TEST 10: Wrong Private Key Decryption (Security Integrity)
    print("Test 10: Security Integrity (Wrong Private Key)")
    try:
        msg = 100
        ct = elgamal.encrypt(pub_key, msg)
        wrong_priv_key = priv_key - 1
        dec = elgamal.decrypt(pub_key, wrong_priv_key, ct)
        # With wrong key, decryption should produce incorrect result
        assert dec != msg
        print(f"  ✓ PASSED - Wrong key produces incorrect decryption")
        print(f"    Original: {msg}, With wrong key: {dec}")
        passed += 1
    except AssertionError:
        print(f"  ✗ FAILED - Wrong key produced correct message (highly unlikely)")
    print()

    # Summary
    print("="*70)
    print(f"                    TEST RESULTS: {passed}/{total} PASSED")
    print("="*70)
    if passed == total:
        print("                  ALL TESTS PASSED SUCCESSFULLY!")
    else:
        print(f"WARNING: {total - passed} test(s) failed")
    print("="*70 + "\n")


if __name__ == "__main__":
    run_tests()