import java.lang.reflect.Array;
import java.util.*;

public class amazon {
    public int[] movieOnFLight(int[] movie_duration, int duration ){
        int[] result = new int[2];
        HashMap<int[],Integer> map = new HashMap<>();
        Arrays.sort(movie_duration);
        int j = movie_duration.length-1;
       // duration = duration-30;
        for(int i = 0 ; i < movie_duration.length; i++){

            if(j < movie_duration.length && j != i){
                int sum = movie_duration[i] + movie_duration[j];
                if(movie_duration[i] + movie_duration[j] <= (duration)){
                    int[] list = new int[]{movie_duration[i],movie_duration[j]};
                    map.put(list,movie_duration[i] + movie_duration[j]);

                }if(sum > duration){
                    j--;
                }
            }
        }
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a,b)-> map.get(b)-map.get(a));
        maxHeap.addAll(map.keySet());
        return maxHeap.remove();
    }


    public int treasureIsland(char[][] island){

        int[] distance = new int[1];
        distance[0] = Integer.MAX_VALUE;
        if(island == null || island.length == 0) return 0;
        int[][] isVisited = new int[island.length][island[0].length];
        dfs(island,distance,0,0,isVisited,0);


        return distance[0];
    }

    public void dfs(char[][] island, int[] distance, int i , int j , int[][] isVisited, int curDistance){
        if(i < 0 || i >= island.length || j < 0 || j >= island[0].length || island[i][j] == 'D' || isVisited[i][j] == 1){
            return;
        }
        isVisited[i][j] = 1;

        if(island[i][j] == 'X'){
            distance[0] = Math.min(distance[0], curDistance);
        }

        dfs(island,distance,i + 1,j,isVisited,curDistance+1);
        dfs(island,distance,i,j + 1 ,isVisited,curDistance+1);
        dfs(island,distance,i -1 ,j,isVisited,curDistance+1);
        dfs(island,distance,i,j -1 ,isVisited,curDistance+1);


    }

    public int treasureIslandII(char[][] island){


        if(island == null || island.length == 0) return -1;
        int minDis = 1;
        Queue<int[]> que = new LinkedList<>();
        for(int i = 0 ; i < island.length ; i++){
            for(int j = 0 ; j < island[i].length ; j++){
                if(island[i][j] == 'S'){
                    island[i][j] = 'D';
                    que.offer(new int[]{i,j});
                }
            }
        }
        int[][] dirs = new int[][]{{1,0},{0,1},{-1,0},{0,-1}};

        while(!que.isEmpty()){
            for(int i = 0 ; i < que.size(); i++){
                int[] cur = que.poll();
                for(int[] dir : dirs){
                    int row = cur[0] + dir[0];
                    int col =  cur[1] + dir[1];
                    if(row < 0 || row >= island.length || col < 0 || col >= island[0].length || island[row][col] == 'D'){
                        continue;
                    }
                    if(island[row][col] == 'X') return minDis;
                    island[row][col] = 'D';
                    que.add(new int[]{row,col});
                }
            }
            minDis++;
        }
        return -1;
    }

    //post office
    public int[][] kClosestPostOffice(int[][] points, int K) {
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a,b)-> (b[0]*b[0]+b[1]*b[1])-(a[0]*a[0]+a[1]*a[1]));
        for(int[] point : points){
            maxHeap.add(point);
            if(maxHeap.size()>K){
                maxHeap.remove();
            }
        }

        int[][] result = new int[K][2];
        while(K > 0){
            K--;
            result[K] = maxHeap.remove();

        }
        return result;
    }

    public int diceRoll(int[] nums) {
        int min = Integer.MAX_VALUE;
        for (int i = 1; i <= 6; i++) {
            int tmp =0;
            for(int j=0; j< nums.length; j++){
                if(i == nums[j])
                    continue;

                if(i+nums[j] ==7){
                    tmp += 2;
                }
                else{
                    tmp++;
                }
            }
            min = Math.min(min, tmp);
        }

        return min;

    }

    public long fillTruck(int num , ArrayList<Integer> boxes, int unitSize, ArrayList<Integer> unitsPerBox , long truckSize ){
        long res = 0;

        if (boxes == null || boxes.size() == 0 || unitsPerBox == null
                || unitsPerBox.size() == 0 || truckSize <= 0 || num < 1) {
            return res;
        }

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int i=0; i<num; i++){
            int box = boxes.get(i);
            int unit = unitsPerBox.get(i);
            while(box > 0){
                maxHeap.add(unit);
                box--;
            }
        }

        while(truckSize >  0){
            if (!maxHeap.isEmpty()){
                res += maxHeap.poll();
            }
            truckSize--;
        }

        return res;
    }
    public static int fillTheTruck(int num, List<Integer> boxes, int unitSize, List<Integer> unitsPerBox, int truckSize) {

        // WRITE YOUR BRILLIANT CODE HERE

        int ans = 0;
        //return 0;
        int boxesLeft = truckSize;

        ArrayList<Map.Entry<Integer, Integer>> unitsBoxes = new ArrayList<>();
        for (int i = 0; i < unitSize; i++)
            unitsBoxes.add(Map.entry(unitsPerBox.get(i), boxes.get(i)));

        unitsBoxes.sort(Comparator.comparing(e -> -e.getKey()));

        for (Map.Entry<Integer, Integer> e : unitsBoxes) {
            int units = e.getKey();
            int box = Math.min(boxesLeft, e.getValue());
            ans += box * units;
            boxesLeft -= box;

            if (boxesLeft == 0)
                break;
        }

        return ans;

    }

    TreeNode maxNode = null;
    double max = Double.MIN_VALUE;
    public TreeNode maxAverage(TreeNode root){
        if(root == null) return null;
        helper(root);
        return maxNode;
    }

    private double[] helper(TreeNode root){
        if(root == null) return new double[]{0,0};

        double total = root.val;
        double count  = 1;
        if(root.children != null){
            for(TreeNode node : root.children){
                double[] cur = helper(root);
                total += cur[0];
                count += cur[1];
            }
        }
        double avg = total/count;
        if(count > 1 && avg > max){
            max = avg;
            maxNode = root;
        }
        return new double[] {total, count};
    }
    public static class TreeNode{
        int val ;
        List<TreeNode> children;
        public TreeNode(){}
        public TreeNode(int val){
            this.val = val;
        }
        public TreeNode(int val , List<TreeNode> children){
            this.val = val;
            this.children = children;
        }
    }


    public static void main(String[] args){
    //movie_duration: [90, 85, 75, 60, 120, 150, 125]
        //{27, 1,10, 39, 12, 52, 32, 67, 76}
        //d: 250
        //movie on flight
        amazon prog = new amazon();
        //int[] movie_duration = new int[]{90, 85, 75, 60, 120, 150, 125};
        //int d = 250;
        int[] movie_duration = new int[]{27, 1,10, 39, 12, 52, 32, 67, 76};
        int d = 77;
        int[] res = prog.movieOnFLight(movie_duration, d);
        System.out.println(res[0]+" "+ res[1]);
        //-------------------------

        //treasure island
        char[][] island = new char[][]{
                {'O', 'O', 'O', 'O'},
                {'D', 'O', 'D', 'O'},
                {'O', 'O', 'O', 'O'},
                {'X', 'D', 'D', 'O'}
        };
        //System.out.println("path treasure island :"+prog.treasureIsland(island));
        //----------------------------

        //treasure island -II
        char[][] islandii = new char[][]{
                {'S', 'O', 'O', 'S', 'S'},
                {'D', 'O', 'D', 'O', 'D'},
                 {'O', 'O', 'O', 'O', 'X'},
                {'X', 'D', 'D', 'O', 'O'},
                {'X', 'D', 'D', 'D', 'O'}
        };
        System.out.println("path treasure island II :"+prog.treasureIslandII(islandii));
        //-------------

        //k closest post office
        int[][] postoffice = new int[][]{{-16, 5}, {-1, 2}, {4, 3}, {10, -2}, {0, 3}, {-5, -9}};
        int[][] res2 = (prog.kClosestPostOffice(postoffice, 3));
        System.out.println("post office result");
        for(int[] re : res2){
            System.out.println(re[0]+ " " +re[1]);
        }
        //------------------------

        // dice roll
        //Input: [6, 5, 4]
        //Input: [6, 6, 1]
        //Input: [6, 1, 5, 4]
        int [] dice = new int[]{6, 5, 4};
        System.out.println("dice roll "+prog.diceRoll(dice));



        //fill the truck
        int n1 = 3;
        int unitSize1 = 3;
        ArrayList<Integer> boxes1 = new ArrayList<>(Arrays.asList(1,2,3));
        ArrayList<Integer> unitsPerBox1 = new ArrayList<>(Arrays.asList(3,2,1));
        int truckSize1 = 4;
        System.out.println("fill the truck "+prog.fillTruck(n1, boxes1,unitSize1, unitsPerBox1, truckSize1 ));
        System.out.println("fill the truck "+prog.fillTheTruck(n1, boxes1,unitSize1, unitsPerBox1, truckSize1 ));


        // Input:
        //              20
        //            /   \
        //          12     18
        //       /  |  \   / \
        //     11   2   3 15  8
        TreeNode left = new TreeNode(12);
        left.children = Arrays.asList(new TreeNode(11), new TreeNode(2), new TreeNode(3));

        TreeNode right = new TreeNode(18);
        right.children = Arrays.asList(new TreeNode(15), new TreeNode(8));

        TreeNode root = new TreeNode(20);
        root.children = Arrays.asList(left, right);

        //output: 18
        TreeNode maxNode = prog.maxAverage(root);
        System.out.println("Max Average: " + maxNode.val);
    }


}
