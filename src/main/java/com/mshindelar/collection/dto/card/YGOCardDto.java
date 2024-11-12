package com.mshindelar.collection.dto.card;

import com.mshindelar.collection.YGOPROApi.dto.Attribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YGOCardDto extends CardDto {
    private String type;
    private int level;
    private String race;
    private String archetype;
    private Attribute attribute;
    private String description;
    private int attack;
    private int defense;

    private int scale;

    private int linkValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        YGOCardDto that = (YGOCardDto) o;
        return level == that.level &&
                attack == that.attack &&
                defense == that.defense &&
                type.equals(that.type) &&
                race.equals(that.race) &&
                archetype.equals(that.archetype) &&
                attribute == that.attribute &&
                description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, level, race, archetype, attribute, description, attack, defense);
    }
}
