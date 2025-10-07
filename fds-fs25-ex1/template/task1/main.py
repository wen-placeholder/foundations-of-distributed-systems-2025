import json
import sys
from pathlib import Path
from typing import Dict, List, Tuple, Iterable, Set


def elementwise_max(a: List[int], b: List[int]) -> List[int]:
    """Return the element-wise maximum between two integer lists."""
    return [max(x, y) for x, y in zip(a, b)]


#  Task 1: Compute Vector Clocks
def compute_vector_clocks(repo: Dict[str, Dict[str, List[str]]]) -> Dict[str, List[int]]:
    """
    Compute vector clocks for each commit in a Git-like DAG.

    Args:
        repo: Dictionary in the form:
              {
                "BranchName": {
                  "CommitName": [list_of_parent_commits]
                }
              }

    Returns:
        A dictionary mapping each commit to its vector clock:
        {
          "CommitName": [vector_clock_values]
        }
    """

    # Determine branch order and assign indices
    branches = sorted(repo.keys())
    n = len(branches)
    b2idx = {b: i for i, b in enumerate(branches)}

    print("Branch order:", branches)
    print("Branch-to-index mapping:", b2idx)

    #  parents: commit → list of parent commits
    parents: Dict[str, List[str]] = {}
    #  commit_branch: commit → branch name
    commit_branch: Dict[str, str] = {}
    for branch, commits in repo.items():
        for commit, ps in commits.items():
            parents[commit] = list(ps)
            commit_branch[commit] = branch

    all_commits = list(parents.keys())
    vc: Dict[str, List[int]] = {}

    print("Parents (commit → list of parents):")
    for k, v in parents.items():
        print(f"  {k}: {v}")

    print("Commit → Branch mapping:")
    for k, v in commit_branch.items():
        print(f"  {k}: {v}")

    # Iteratively compute vector clocks (topological-like traversal)
    print("Start computing vector clocks...")
    remaining = set(all_commits)
    print(f"Commits not yet computed: {remaining}")

    while remaining:
        progressed = False
        for c in all_commits:
            # Only compute when all parents' vector clocks are available
            if c in remaining and all(p in vc for p in parents[c]):
                cur = [0] * n
                for p in parents[c]:
                    cur = elementwise_max(cur, vc[p])
                cur[b2idx[commit_branch[c]]] += 1
                vc[c] = cur
                remaining.remove(c)
                print(f"Computed {c}: {cur}")
                progressed = True

        if not progressed:
            # Cannot proceed further: missing or cyclic dependencies
            missing = {c: [p for p in parents[c] if p not in vc] for c in remaining}
            raise ValueError(
                f"Cannot resolve vector clocks; missing or cyclic parents: {missing}"
            )

    print("All vector clocks computed successfully!")
    ordered_vc = {c: vc[c] for c in all_commits if c in vc}

    print("Final results (preserving input order):")
    print(json.dumps(ordered_vc, indent=2, ensure_ascii=False))

    return ordered_vc

#  Task 2: Causal Precedence and Full Causal Graph
def causally_precedes(a: List[int], b: List[int]) -> bool:
    """
    Check whether vector clock a causally precedes b (a → b).

    Rule:
      a[i] <= b[i] for all i, and
      a[j] <  b[j] for at least one j.
    """
    leq_all = all(x <= y for x, y in zip(a, b))
    lt_any = any(x < y for x, y in zip(a, b))
    return leq_all and lt_any


def are_concurrent(a: List[int], b: List[int]) -> bool:
    # Two events are concurrent if neither causally precedes the other
    return not causally_precedes(a, b) and not causally_precedes(b, a)


def build_causal_edges(clocks: Dict[str, List[int]]) -> Set[Tuple[str, str]]:
    """
    Build the full causal graph (may include transitive edges).
    For each pair (u, v), add an edge u → v if VC[u] causally precedes VC[v].
    """
    commits = list(clocks.keys())
    edges: Set[Tuple[str, str]] = set()
    for u in commits:
        for v in commits:
            if u != v and causally_precedes(clocks[u], clocks[v]):
                edges.add((u, v))
    return edges


#  Task 3: Transitive Reduction (Minimal Causal Graph)
def _dfs(start: str, target: str, adj: Dict[str, List[str]]) -> bool:
    # Helper: DFS to test reachability from start to target.
    stack = [start]
    seen = {start}
    while stack:
        x = stack.pop()
        if x == target:
            return True
        for y in adj.get(x, []):
            if y not in seen:
                seen.add(y)
                stack.append(y)
    return False


def transitive_reduction(nodes: Iterable[str], edges: Set[Tuple[str, str]]) -> Set[Tuple[str, str]]:
    """
    Compute a transitive reduction of a DAG (remove redundant edges).

    For each edge (u → v), temporarily remove it and check if v
    is still reachable from u via other paths. If yes, remove it permanently.
    """
    adj: Dict[str, List[str]] = {}
    for u, v in edges:
        adj.setdefault(u, []).append(v)

    reduced = set(edges)
    for (u, v) in list(edges):
        adj[u].remove(v)
        if _dfs(u, v, adj):  # still reachable → redundant edge
            reduced.discard((u, v))
        else:
            adj[u].append(v)
    return reduced

# Write the graph to a Graphviz .dot file for visualization.
def write_dot(path: str, nodes: Iterable[str], edges: Set[Tuple[str, str]]) -> None:
    nodes = list(nodes)
    with open(path, "w", encoding="utf-8") as f:
        f.write("digraph G {\n")
        f.write("  rankdir=LR;\n")  # left-to-right layout
        for n in nodes:
            f.write(f'  "{n}" [shape=box];\n')
        for u, v in edges:
            f.write(f'  "{u}" -> "{v}";\n')
        f.write("}\n")


if __name__ == "__main__":
    # Default input JSON file
    # Example usage: python main.py example.json
    if len(sys.argv) > 1:
        in_path = Path(sys.argv[1])
        with in_path.open("r", encoding="utf-8") as f:
            data = json.load(f)
    else:
        # Default: load task1/data.json relative to script location
        script_dir = Path(__file__).parent
        data_path = script_dir / "data.json"
        data = json.load(open(data_path, "r", encoding="utf-8"))
        in_path = data_path

    # -------- Task 1: Compute Vector Clocks --------
    clocks = compute_vector_clocks(data)

    # Save JSON result
    out_path = in_path.with_suffix(".clocks.json")
    out_path.write_text(json.dumps(clocks, indent=2, ensure_ascii=False), encoding="utf-8")
    print(f"Saved vector clocks to: {out_path}")

    # -------- Task 2: Build Full Causal Graph --------
    full_edges = build_causal_edges(clocks)
    write_dot(in_path.with_suffix(".causal_full.dot"), clocks.keys(), full_edges)
    print(f"Saved full causal graph to: {in_path.with_suffix('.causal_full.dot')}")

    # -------- Task 3: Transitive Reduction --------
    reduced_edges = transitive_reduction(clocks.keys(), full_edges)

    write_dot(in_path.with_suffix(".causal_min.dot"), clocks.keys(), reduced_edges)
    print(f"Saved minimal causal graph to: {in_path.with_suffix('.causal_min.dot')}")
