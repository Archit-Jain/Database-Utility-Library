import java.util.*;

public class test {


    public static void main(String[] args){
        int[][] arr = new int[][]{{1,2,5},{3,4}};
        System.out.println(arr[0][0] +" " +arr[0][1] +" "+arr[0][2]+" "+arr[1][0] +" "+arr[1][1] +" " );

        HashMap<Integer, List<Integer>> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        map.put(1,list);
        List<Integer> list2 = new ArrayList<>();
        list2.add(4);
        list2.add(5);
        map.put(3,list2);

        List<Integer> list3 = new ArrayList<>();
        list3.add(7);
        list3.add(8);
        list3.add(9);
        list3.add(10);

        map.put(2,list3);

        int[][] result = new int[map.size()][];
        int idx = 0;
//        int[] blank = new int
        for(List<Integer> li : map.values()){
//            result[idx] = li.toArray(new int[li.size()]);
            int[] newArr = new int[li.size()];

            for(int i = 0 ; i < li.size(); i++){
                newArr[i] = li.get(i);
            }
            Arrays.sort(newArr);
            result[idx] = newArr;
            idx++;
        }

        System.out.println("ok");
    }
    public static int levelUp(int k, List<Integer> score) {
        if(k <= 0) return 0;
        Collections.sort(score, Collections.reverseOrder());
        int rank = 1;
        int res = 0;
        for(int i = 0; i < score.size(); i++) {
            if(i == 0) {
                rank = 1;
            } else if(score.get(i) != score.get(i - 1)) {
                rank = i + 1;
            }
            if(rank <= k && score.get(i) > 0) res++;
            else break;
        }
        return res;
    }

    public int findLeastNumOfUniqueInts(int[] arr, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for(int key: arr) {
            map.put(key, map.getOrDefault(key,0)+1);
        }

        int index = 0;
        int[] count = new int[map.size()];
        for(Map.Entry<Integer, Integer> obj: map.entrySet()) {
            count[index++] = obj.getValue();
        }

        index = 0;
        int sum = 0;
        Arrays.sort(count);
        for(; index < count.length; index++) {
            sum += count[index];
            if(sum >= k) {
                break;
            }
        }

        return (sum == k) ? count.length-index-1:count.length-index;
    }
}

