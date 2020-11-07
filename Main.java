import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

public class Main {
    /**
     * Iterate through each line of input.
     */
    public static void main(String[] args) throws IOException {
        InputStreamReader reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        BufferedReader in = new BufferedReader(reader);
        String line;
        while ((line = in.readLine()) != null) {
            Main.matchBenchmark(line);
        }
    }

    // A class for assets which holds their name, type (ie bond or stock) and shares.
    // Note that BigDecimal is used as it is more reliable for financial transactions:
    public static class asset{
        public String name;
        public String type;
        public BigDecimal shares;

        public asset(String name, String type, BigDecimal shares){
            this.name = name;
            this.type = type;
            this.shares = shares;
        }

        public String getName(){
            return name;
        }

        public String getType(){
            return type;
        }

        public BigDecimal getShares(){
            return shares;
        }

        // Returns the transaction to get from the portfolio asset to the benchmark value.
        public transaction getTransaction(asset bench){
            BigDecimal diff = bench.getShares().subtract(shares);

            if(diff.compareTo(BigDecimal.ZERO) == 1){
                return new transaction(bench.getName() + bench.getType().charAt(0), "BUY", new asset(bench.getName(), bench.getType(), diff.abs()));
            }
            else{
                return new transaction(bench.getName() + bench.getType().charAt(0), "SELL", new asset(bench.getName(), bench.getType(), diff.abs()));
            }
        }

        // A sell transaction for all of the asset:
        public transaction sellAsset(){
            return new transaction(name + type.charAt(0), "SELL", new asset(name, type, shares));
        }

        // A buy transaction for all of the asset:
        public transaction buyAsset(){
            return new transaction(name + type.charAt(0), "BUY", new asset(name, type, shares));
        }

        // Checks if assets are the same:
        public boolean sameAsset(asset bench){
            if(name.equals(bench.getName()) && type.equals(bench.getType())){
                return true;
            }
            else{
                return false;
            }
        }
    }

    // A class for transactions, holds sort code, type (BUY or SELL) and the asset itself:
    public static class transaction implements Comparable<transaction>{
        public String sortCode;
        public String type;
        public asset item;

        public transaction(String sortCode, String type, asset item){
            this.sortCode = sortCode;
            this.type = type;
            this.item = item;
        }

        public String getSortCode(){
            return sortCode;
        }

        // Converts the transaction object to a string to output:
        public String convertString(){
            return (type + "," + item.name + "," + item.getType() +"," + item.getShares());
        }

        // Overrides compare so we can sort the transactions:
        @Override
        public int compareTo(transaction t) {
            return sortCode.compareTo(t.getSortCode());
        }
    }

    public static void matchBenchmark(String input) {

        //---------------------------------------------------------------------->
        //----Two edge case scenarios (Code is explained in main branch below):
        //---------------------------------------------------------------------->

        // First we check if both portfolio and benchmark is empty:
        if(input.equals(":")){
            System.out.println();
        }

        // Then check if the benchmark is empty, if so sell all portfolio assets:
        else if(input.endsWith(":")){
            ArrayList<transaction> transactions = new ArrayList<>();
            String[] portfolioStr = input.split(":")[0].split("\\|");

            for(int i = 0; i < portfolioStr.length; i++) {
                String[] assetStr = portfolioStr[i].split(",");
                asset portAsset = new asset(assetStr[0], assetStr[1], new BigDecimal(assetStr[2]));
                transactions.add(portAsset.sellAsset());
            }

            Collections.sort(transactions);
            for(int i = 0; i < transactions.size(); i++){
                System.out.println(transactions.get(i).convertString());
            }
        }

        //---------------------------------------------------------------------->
        //---- Main branch (Usual case):
        //---------------------------------------------------------------------->

        else{
            // Create a list of transactions to output:
            ArrayList<transaction> transactions = new ArrayList<>();

            // Split out each asset of the benchmark into an arraylist using the class above:
            String[] benchmarkStr = input.split(":")[1].split("\\|");
            ArrayList<asset> benchmark = new ArrayList<>();
            for(int i = 0; i < benchmarkStr.length; i++){
                String[] assetStr = benchmarkStr[i].split(",");
                benchmark.add(new asset(assetStr[0], assetStr[1], new BigDecimal(assetStr[2])));
            }

            // Similar with the portfolio assets except we compare them individually to the benchmark list.
            // We also check to see if the portfolio provided is empty or not:
            String[] portfolioStr = input.split(":")[0].split("\\|");
            if(portfolioStr[0]!= ""){
                for(int i = 0; i < portfolioStr.length; i++){
                    String[] assetStr = portfolioStr[i].split(",");
                    asset portAsset = new asset(assetStr[0], assetStr[1], new BigDecimal(assetStr[2]));
                    for(int j = 0; j < benchmark.size(); j++){
                        // If they are the same asset name and type but not number, create transaction between two.
                        // If they are the same in number, no transaction needs to be made, so remove from list:
                        if(portAsset.sameAsset(benchmark.get(j))){
                            if(!portAsset.getShares().equals(benchmark.get(j).getShares())){
                                transactions.add(portAsset.getTransaction(benchmark.get(j)));
                                benchmark.remove(j);
                                break;
                            }
                            else{
                                benchmark.remove(j);
                                break;
                            }
                        }

                        // If the asset does not exist in the benchmark, sell all:
                        else if(j == benchmark.size()){
                            transactions.add(portAsset.sellAsset());
                        }
                    }
                }
            }

            // If the assets in the benchmark are not in the portfolio, buy all:
            for(int i = 0; i < benchmark.size(); i++){
                transactions.add(benchmark.get(i).buyAsset());
            }

            // Sort the list of transactions to output in the correct order:
            Collections.sort(transactions);
            for(int i = 0; i < transactions.size(); i++){
                System.out.println(transactions.get(i).convertString());
            }
        }
    }
}
