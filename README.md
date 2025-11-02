# Assignment 4 Report - Smart City Scheduling

## Project Overview
This project implements a comprehensive graph analysis system for smart city task scheduling, combining Strongly Connected Components (SCC) detection, topological ordering, and shortest/longest path algorithms in Directed Acyclic Graphs (DAGs). The system processes task dependency graphs to identify cyclic dependencies, compress them into components, and compute optimal execution orders.

## Algorithms Implemented

### 1. Strongly Connected Components (SCC)
- **Algorithm**: Tarjan's algorithm
- **Time Complexity**: O(V + E)
- **Features**:
    - Detects all strongly connected components
    - Builds condensation graph (DAG of components)
    - Handles cyclic dependencies effectively

### 2. Topological Sorting
- **Algorithm**: Kahn's algorithm
- **Time Complexity**: O(V + E)
- **Features**:
    - Computes valid execution order for DAG
    - Detects cycles in the graph
    - Works with both weighted and unweighted graphs

### 3. DAG Shortest/Longest Paths
- **Algorithm**: Dynamic programming over topological order
- **Time Complexity**: O(V + E)
- **Features**:
    - Single-source shortest paths
    - Critical path (longest path) analysis
    - Path reconstruction for optimal sequences

## Dataset Summary

The system was tested on 9 generated datasets across three categories:

| Category | Nodes Range | Edges Range | Density | Avg SCCs | Avg Time (ms) |
|----------|-------------|-------------|---------|----------|---------------|
| Small    | 6-12        | 10-31       | 0.3-0.5 | 2-4      | 1-5           |
| Medium   | 15-20       | 20-82       | 0.3-0.5 | 4-5      | 3-7           |
| Large    | 25-50       | 94-569      | 0.3-0.5 | 5-10     | 4-12          |

## Performance Analysis

### SCC Detection (Tarjan's Algorithm)
- **Performance**: Linear time complexity O(V+E) confirmed through testing
- **Bottlenecks**: DFS recursion depth for large connected components
- **Optimizations**: Efficient stack management and low-link computation
- **Metrics**: Average 0.01-0.7ms per graph depending on size and connectivity

### Topological Sort (Kahn's Algorithm)
- **Performance**: Efficient O(V+E) with queue operations
- **Observations**: Most effective for sparse DAGs with low indegree distributions
- **Counters**: Tracks queue operations (pops/pushes) and edge processing
- **Cycle Detection**: Automatically detects and reports cycles in input graphs

### DAG Path Algorithms
- **Shortest Paths**: Computes distances from source using edge relaxation
- **Longest Paths**: Finds critical path via sign inversion approach
- **Advantage**: No need for priority queues unlike general graph algorithms
- **Efficiency**: Leverages topological order for optimal O(V+E) performance

## Key Results and Findings

### 1. SCC Compression Effectiveness
- Successfully identified and compressed cyclic dependencies
- Condensation graphs were significantly smaller than original graphs
- Enabled application of DAG algorithms to originally cyclic graphs

### 2. Topological Ordering Reliability
- Generated valid execution orders for all acyclic components
- Maintained dependency constraints in scheduling
- Provided foundation for efficient path computation

### 3. Path Analysis Insights
- Shortest paths identified optimal task sequences
- Longest paths revealed critical paths and bottlenecks
- Weighted edges effectively modeled real-world constraints

### 4. Scalability Performance
- Linear scaling observed with graph size increases
- Efficient handling of graphs up to 50 nodes and 569 edges
- Memory usage remained manageable across all test cases

## Technical Implementation Details

### Architecture
- **Modular Design**: Separate packages for SCC, topological sort, and DAG algorithms
- **Metrics System**: Comprehensive performance tracking with operation counters
- **Data Generation**: Configurable graph generation with control over density and connectivity

### Testing Coverage
- **Unit Tests**: 27 tests covering all major components
- **Edge Cases**: Empty graphs, single nodes, self-loops, disconnected components
- **Integration**: End-to-end pipeline testing with real data

### Code Quality
- **Readability**: Clear naming conventions and structured code
- **Documentation**: Javadoc comments for public APIs
- **Error Handling**: Robust exception handling for invalid inputs

## Practical Applications

### Smart City Use Cases
1. **Task Scheduling**: Optimize maintenance and service tasks
2. **Dependency Management**: Handle cyclic dependencies in city operations
3. **Critical Path Analysis**: Identify bottlenecks in project timelines
4. **Resource Allocation**: Efficiently schedule limited resources

### Algorithm Selection Guidelines
- **Use SCC + Topological Sort** when dependencies may contain cycles
- **Use DAG Shortest Paths** for minimizing cost/duration
- **Use DAG Longest Paths** for identifying critical paths
- **Combine approaches** for complex scheduling problems

## Conclusions and Recommendations

### Performance Conclusions
1. **SCC Detection** is essential for handling real-world cyclic dependencies
2. **Topological Ordering** provides reliable execution sequences
3. **DAG Algorithms** offer optimal performance for scheduling problems
4. **The implemented system** efficiently handles medium-scale city scheduling problems

### Practical Recommendations
1. **For small graphs** (<20 nodes): All algorithms perform excellently
2. **For medium graphs** (20-50 nodes): System scales well with linear time complexity
3. **For larger graphs** (>50 nodes): Consider iterative DFS variants for SCC
4. **Production use**: Add persistence layer and visualization capabilities

### Future Enhancements
1. Parallel processing for large graphs
2. Real-time graph updates and incremental algorithms
3. Visualization components for result interpretation
4. Integration with actual city management systems

The project successfully demonstrates that graph algorithms can effectively solve complex scheduling problems in smart city contexts, providing both theoretical guarantees and practical performance for real-world applications.