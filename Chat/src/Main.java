/**
 * @author Johan Ehrencrona joeh2789
 */
public class Main {

    /**
     * @param input takes up to two parameters one for host and one for port if no arguments are entered standardizes to 127.0.0.1 and 2000.
     * @throws Exception if too many arguments are entered
     */
    public static void main(String[] input) throws Exception {
        switch (input.length){
            case 0:
                new Client("127.0.0.1", 2000);
                break;
            case 1:
                new Client(input[0],2000);
                break;
            case 2:
                new Client(input[0],Integer.parseInt(input[1]));
                break;
        }
        if(input.length>2) throw new Exception("Error: Too many arguments (" + input.length + ")");
    }
}
