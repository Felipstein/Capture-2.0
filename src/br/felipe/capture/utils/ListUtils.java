package br.felipe.capture.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Predicate;

public class ListUtils {
	
	public static ArrayList<String> getElementsStartingWith(String startWith, ArrayList<String> elements) {
		return getElementsStartingWith(startWith, false, elements);
	}
	
	public static ArrayList<String> getElementsStartingWith(String startWith, HashSet<String> elements) {
		return getElementsStartingWith(startWith, false, new ArrayList<String>(elements));
	}
	
	public static ArrayList<String> getElementsStartingWith(String startWith, String... elements) {
		return getElementsStartingWith(startWith, false, elements);
	}
	
	public static ArrayList<String> getElementsStartingWith(String startWith, boolean ignoreCase, HashSet<String> elements) {
		return getElementsStartingWith(startWith, ignoreCase, new ArrayList<String>(elements));
	}
	
	public static ArrayList<String> getElementsStartingWith(String startWith, boolean ignoreCase, ArrayList<String> elements) {
		return new ArrayList<String>(elements.stream().filter(x -> ignoreCase ? x.toLowerCase().startsWith(startWith.toLowerCase()) : x.startsWith(startWith)).collect(Collectors.toList()));
	}
	
	public static ArrayList<String> getElementsStartingWith(String startWith, boolean ignoreCase, String... elements) {
		return getElementsStartingWith(startWith, ignoreCase, toArrayList(elements));
	}
	
	public static <T> ArrayList<T> toArrayList(T[] obj) {
		ArrayList<T> lista = new ArrayList<T>();
		for(int i = 0; i < obj.length; i++) {
			lista.add(obj[i]);
		}
		return lista;
	}
	
	public static <T> ArrayList<T> toArrayList(Stream<T> obj) {
		return (ArrayList<T>) obj.collect(Collectors.toList());
	}
	
	public static <T> HashSet<T> toHashSet(Stream<T> obj) {
		return new HashSet<T>(obj.collect(Collectors.toList()));
	}
	
	public static <T> ArrayList<T> filter(ArrayList<T> obj, Predicate<? super T> predicate) {
		return toArrayList(obj.stream().filter(predicate));
	}
	
	public static <T> HashSet<T> filter(HashSet<T> obj, Predicate<? super T> predicate) {
		return toHashSet(obj.stream().filter(predicate));
	}
	
	public static <T> T getFirstElement(Collection<T> obj) {
		Iterator<T> i = obj.iterator();
		return i.next();
	}
	
	public static <T> T getRandomElement(@SuppressWarnings("unchecked") T... elements) {
		return getRandomElement(toArrayList(elements));
	}
	
	public static <T> T getRandomElement(ArrayList<T> elements) {
		return elements.get(new Random().nextInt(elements.size()));
	}
	
}