package hansanhha;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectorExample {

    public static void main(String[] args) {

        String[] originalDataSource = new String[]{"1", "2", "3", "4", "5"};

        LinkedList<String> toCollectionLinkedList = Stream.of(originalDataSource)
                .collect(Collectors.toCollection(LinkedList::new));

        Map<String, Integer> collect = Stream.of(originalDataSource)
                .collect(Collectors.toMap(s -> s, Integer::valueOf));
    }
}
