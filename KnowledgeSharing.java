package Assignment3;

import java.util.*;

//Class to represent an Island with its properties
class Island {
 int id;
 int population;
 Date lastVisitTime; // Assuming Date for simplicity

 public Island(int id, int population, Date lastVisitTime) {
     this.id = id;
     this.population = population;
     this.lastVisitTime = lastVisitTime;
 }
}

public class KnowledgeSharing {

 // Dijkstra's algorithm to find shortest paths from a single source
 public static int[] dijkstra(int[][] graph, int start) {
     int n = graph.length;
     int[] dist = new int[n];
     Arrays.fill(dist, Integer.MAX_VALUE);
     dist[start] = 0;

     PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(v -> dist[v]));
     pq.offer(start);

     while (!pq.isEmpty()) {
         int u = pq.poll();

         for (int v = 0; v < n; v++) {
             if (graph[u][v] > 0 && dist[v] > dist[u] + graph[u][v]) {
                 dist[v] = dist[u] + graph[u][v];
                 pq.offer(v);
             }
         }
     }

     return dist;
 }

 // TSP Solver with additional constraints (population and least recent visit time)
 public static List<Integer> solveTSPWithConstraints(int[][] graph, Island[] islands) {
     int n = islands.length;
     int[][] dp = new int[1 << n][n];
     int[][] prev = new int[1 << n][n];
     int finalMask = (1 << n) - 1;

     // Compute shortest paths using Dijkstra's for each island
     int[][] shortestPaths = new int[n][n];
     for (int i = 0; i < n; i++) {
         shortestPaths[i] = dijkstra(graph, i);
     }

     // Dynamic Programming approach to solve TSP with constraints
     for (int mask = 1; mask <= finalMask; mask++) {
         Arrays.fill(dp[mask], Integer.MAX_VALUE);
     }
     dp[1][0] = 0; // Starting from island 0

     for (int mask = 1; mask <= finalMask; mask++) {
         for (int last = 0; last < n; last++) {
             if ((mask & (1 << last)) == 0) continue;
             for (int next = 0; next < n; next++) {
                 if (last == next || (mask & (1 << next)) != 0) continue;
                 int newMask = mask | (1 << next);
                 int newCost = dp[mask][last] + shortestPaths[last][next];

                 // Apply additional constraints (population and least recent visit time)
                 int populationDiff = islands[next].population - islands[last].population;
                 long lastVisitDiff = islands[next].lastVisitTime.getTime() - islands[last].lastVisitTime.getTime();
                 if (newCost < dp[newMask][next] ||
                         (newCost == dp[newMask][next] && (populationDiff > 0 || lastVisitDiff < 0))) {
                     dp[newMask][next] = newCost;
                     prev[newMask][next] = last;
                 }
             }
         }
     }

     // Reconstructing the optimal path
     List<Integer> path = new ArrayList<>();
     int mask = finalMask;
     int curr = 0; // Start from island 0
     while (mask != 0) {
         path.add(curr);
         int next = prev[mask][curr];
         mask ^= (1 << curr);
         curr = next;
     }
     path.add(0); // Return to the starting island to complete the loop

     Collections.reverse(path);
     return path;
 }

 public static void main(String[] args) {
     // Example input data
     int[][] graph = {
             {0, 10, 15, 20},
             {5, 0, 9, 10},
             {6, 13, 0, 12},
             {8, 8, 9, 0}
     };
     Island[] islands = {
             new Island(0, 100, new Date()), // Example population and visit time
             new Island(1, 200, new Date()),
             new Island(2, 150, new Date()),
             new Island(3, 180, new Date())
     };

     // Solve the TSP with constraints
     List<Integer> optimalPath = solveTSPWithConstraints(graph, islands);

     // Output the optimal path
     System.out.println("Optimal TSP path:");
     for (int island : optimalPath) {
         System.out.print(island + " ");
     }
     System.out.println();
 }
}
