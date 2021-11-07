package fr.uge.poo.cmdline.ex5.parser;

public sealed interface Arg permits IntArg, StringArg {

    int asInt();
    String asString();
}

record IntArg(int value) implements Arg {
    @Override
    public int asInt() {
        return value;
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("Is not a String");
    }
}

record StringArg(String value) implements Arg {
    @Override
    public int asInt() {
        throw new UnsupportedOperationException("Is not an integer");
    }

    @Override
    public String asString() {
        return value;
    }
}
