class CLS:
    def __init__(self):
        # Dictionary to store elements, key is element value, value is integer counter
        self.A = {}
    
    def add(self, e):
        """Add element"""
        if e not in self.A:
            self.A[e] = 1  # Initialize to 1 (odd, represents presence)
        elif self.A[e] % 2 == 0:  # If currently even
            self.A[e] = self.A[e] + 1  # Change to odd (represents presence)
        # If already odd, do nothing (idempotent)
    
    def remove(self, e):
        """Remove element"""
        if e in self.A and self.A[e] % 2 == 1:  # If exists and currently odd
            self.A[e] = self.A[e] + 1  # Change to even (represents absence)
        # If element doesn't exist or is already even, do nothing
    
    def contains(self, e):
        """Check if element exists"""
        return (e in self.A) and (self.A[e] % 2 == 1)  # Exists and is odd
    
    def mutual_sync(self, other_lists):
        """Perform bidirectional synchronization with other CLS instances"""
        for other in other_lists:
            self._merge(other)
            other._merge(self)
    
    def _merge(self, other):
        """Merge state from another CLS instance"""
        # Get union of all elements
        all_elements = set(self.A.keys()) | set(other.A.keys())
        
        for e in all_elements:
            # Take the maximum value from both instances
            self_val = self.A.get(e, 0)
            other_val = other.A.get(e, 0)
            self.A[e] = max(self_val, other_val)
    
    def __str__(self):
        """Return current elements in the set (for debugging)"""
        items = [elem for elem in self.A if self.contains(elem)]
        return f"CLS{set(items)}"

# Test code
if __name__ == "__main__":
    # Simulate shared shopping cart scenario
    alice_list = CLS()
    bob_list = CLS()

    alice_list.add('Milk')
    alice_list.add('Potato')
    alice_list.add('Eggs')

    bob_list.add('Sausage')
    bob_list.add('Mustard')
    bob_list.add('Coke')
    bob_list.add('Potato')
    bob_list.mutual_sync([alice_list])

    alice_list.remove('Sausage')
    alice_list.add('Tofu')
    alice_list.remove('Potato')
    alice_list.mutual_sync([bob_list])

    print("Bob's list contains 'Potato'?", bob_list.contains('Potato'))
    print("Alice's list:", alice_list)
    print("Bob's list:", bob_list)
    
    # Additional test: verify odd-even logic
    print("\nAdditional test:")
    test = CLS()
    test.add('Apple')
    print("After adding Apple - count:", test.A.get('Apple'), "exists:", test.contains('Apple'))
    test.remove('Apple')
    print("After removing Apple - count:", test.A.get('Apple'), "exists:", test.contains('Apple'))
    test.add('Apple')
    print("After adding Apple again - count:", test.A.get('Apple'), "exists:", test.contains('Apple'))