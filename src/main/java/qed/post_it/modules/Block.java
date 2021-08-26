package qed.post_it.modules;

public class Block
{
    public String blockName;
    public String blockDescription;

    public Block(String name, String des )
    {
        blockName = name;
        blockDescription = des;
    }

    @Override
    public String toString()
    {
        return String.format("{\"name\": \"%s\", \"des\": \"%s\"}", blockName, blockDescription);
    }
}