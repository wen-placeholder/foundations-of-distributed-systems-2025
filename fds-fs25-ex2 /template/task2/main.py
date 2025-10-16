class CLS:
    def __init__(self):
        # 存储元素的字典，key为元素值，value为整数计数器
        self.A = {}
    
    def add(self, e):
        """添加元素"""
        if e not in self.A:
            self.A[e] = 1  # 初始化为1（奇数，表示存在）
        elif self.A[e] % 2 == 0:  # 如果当前是偶数
            self.A[e] = self.A[e] + 1  # 变为奇数（表示存在）
        # 如果当前已经是奇数，不做任何操作（幂等性）
    
    def remove(self, e):
        """移除元素"""
        if e in self.A and self.A[e] % 2 == 1:  # 如果存在且当前是奇数
            self.A[e] = self.A[e] + 1  # 变为偶数（表示不存在）
        # 如果元素不存在或已经是偶数，不做任何操作
    
    def contains(self, e):
        """检查元素是否存在"""
        return (e in self.A) and (self.A[e] % 2 == 1)  # 存在且为奇数
    
    def mutual_sync(self, other_lists):
        """与其他CLS实例进行双向同步"""
        for other in other_lists:
            self._merge(other)
            other._merge(self)
    
    def _merge(self, other):
        """合并另一个CLS实例的状态"""
        # 获取所有元素的并集
        all_elements = set(self.A.keys()) | set(other.A.keys())
        
        for e in all_elements:
            # 取两个实例中的最大值
            self_val = self.A.get(e, 0)
            other_val = other.A.get(e, 0)
            self.A[e] = max(self_val, other_val)
    
    def __str__(self):
        """返回当前集合中的元素（用于调试）"""
        items = [elem for elem in self.A if self.contains(elem)]
        return f"CLS{set(items)}"

# 测试代码
if __name__ == "__main__":
    # 模拟共享购物车场景
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

    print("Bob's list contains 'Potato' ?", bob_list.contains('Potato'))
    print("Alice's list:", alice_list)
    print("Bob's list:", bob_list)
    
    # 额外测试：验证奇偶逻辑
    print("\n额外测试:")
    test = CLS()
    test.add('Apple')
    print("添加Apple后计数:", test.A.get('Apple'), "存在:", test.contains('Apple'))
    test.remove('Apple')
    print("移除Apple后计数:", test.A.get('Apple'), "存在:", test.contains('Apple'))
    test.add('Apple')
    print("再次添加Apple后计数:", test.A.get('Apple'), "存在:", test.contains('Apple'))