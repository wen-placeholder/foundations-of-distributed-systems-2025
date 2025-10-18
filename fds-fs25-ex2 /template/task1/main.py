import os
import time
import threading
import random

nodes = []
buffer = {} # items are in the form 'node_id': [(msg_type, value)]

class Node:
    def __init__(self,id):
        buffer[id] = []
        self.id = id
        self.working = True

        # ----- Consensus states -----
        self.state = 'follower'
        self.leader_id = None
        self.voted = False
        self.votes_received = 0

        # ----- Timing control -----
        self.last_heartbeat = time.time()
        self.election_started = False
        self.election_start_time = None
        self.election_wait = None

    def start(self):
        print(f'node {self.id} started')
        threading.Thread(target=self.run).start()

    def run(self):
        while True:
            while buffer[self.id]:
                msg_type, value = buffer[self.id].pop(0)
                if self.working:
                    self.deliver(msg_type, value)

            now = time.time()

            # ----- if leader: broadcast heartbeat -----
            if self.state == 'leader' and self.working:
                if now - self.last_heartbeat >= 0.5:
                    self.broadcast('heartbeat', self.id)
                    self.last_heartbeat = now

            # ----- if follower: check leader timeout -----
            if self.state == 'follower' and self.working:
                if now - self.last_heartbeat >= 1.0:
                    # no heartbeat for 1 second, start election
                    self.state = 'candidate'
                    self.election_started = True
                    self.election_start_time = now
                    self.election_wait = random.uniform(1.0, 3.0)
                    self.votes_received = 0
                    self.voted = False
                    print(f'node {self.id}: starting election in {self.election_wait:.1f}s')

            # ----- if candidate: manage election phase -----
            if self.state == 'candidate' and self.working:
                # waiting period before announcing candidacy
                if self.election_started and now - self.election_start_time >= self.election_wait:
                    if self.election_started:
                        print(f'node {self.id}: broadcasting candidacy')
                        self.broadcast('candidacy', self.id)
                        self.voted = True  # vote for self
                        self.votes_received = 1
                        self.election_start_time = now
                        self.election_started = False  # move to vote collection
                    else:
                        self.state = 'follower'
                
                # vote collection phase: check after 2s if majority reached
                elif not self.election_started and self.election_start_time and (now - self.election_start_time >= 2.0):
                    majority = len(nodes) // 2 + 1
                    if self.votes_received >= majority:
                        self.state = 'leader'
                        self.leader_id = self.id
                        print(f'node {self.id}: elected as leader with {self.votes_received} votes!')
                        self.last_heartbeat = now
                    else:
                        print(f'node {self.id}: failed election ({self.votes_received} votes)')
                        self.state = 'follower'
                        self.voted = False
                        self.last_heartbeat = now


            time.sleep(0.1)

    def broadcast(self, msg_type, value):
        if self.working:
            for node in nodes:
                buffer[node.id].append((msg_type,value))
    
    def crash(self):
        if self.working:
            self.working = False
            buffer[self.id] = []

            self.state = None
            print(f'node {self.id} crashed')
    
    def recover(self):
        if not self.working:
            buffer[self.id] = []
            self.working = True

            self.state = 'follower'
            self.voted = False
            self.leader_id = None
            self.last_heartbeat = time.time()
            self.election_started = False
            print(f'node {self.id} recovered')

    def deliver(self, msg_type, value):

        # ----- HEARTBEAT -----
        if msg_type == 'heartbeat':
            leader_id = value
            if self.id != leader_id:
                self.last_heartbeat = time.time()
                if self.state != 'follower' or self.leader_id != leader_id:
                    print(f'node {self.id} got a heartbeat and followed node {leader_id} as leader')
                self.state = "follower"
                self.election_started = False
                self.voted = False
                self.leader_id = leader_id

        # ----- CANDIDACY -----
        elif msg_type == 'candidacy':
            candidate_id = value
            # if this node is also waiting to become candidate, resign
            if self.state == 'candidate' and self.election_started and candidate_id != self.id:
                self.state = 'follower' 
                self.election_started = False

            # if this node has not voted, vote for the candidate
            if not self.voted:
                self.voted = True
                self.broadcast('vote', (self.id, candidate_id))
                print(f'node {self.id}: voted for {candidate_id}')

        # ----- VOTE -----
        elif msg_type == 'vote':
            # voter_id, candidate_id = value
            candidate_id = value[1]
            if self.state == 'candidate' and candidate_id == self.id:
                self.votes_received += 1


            

def initialize(N):
    global nodes
    nodes = [Node(i) for i in range(N)]
    for node in nodes:
        node.start()

if __name__ == "__main__":
    os.system('clear')
    N = 6
    initialize(N)
    print('actions: state, crash, recover')
    while True:
        act = input('\t$ ')
        if act == 'crash' : 
            id = int(input('\tid > '))
            if 0<= id and id<N: nodes[id].crash()
        elif act == 'recover' : 
            id = int(input('\tid > '))
            if 0<= id and id<N: nodes[id].recover()
        elif act == 'state':
            for node in nodes:
                print(f'\t\tnode {node.id}: {node.state}')

