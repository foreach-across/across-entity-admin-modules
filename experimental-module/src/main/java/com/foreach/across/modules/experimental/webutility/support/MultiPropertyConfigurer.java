package com.foreach.across.modules.experimental.webutility.support;

import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Helper that allows configuring multiple properties in one go.
 * Multiple {@code Consumer} instances can be added using {@link #enable(Consumer)}, all of them
 * will then be applied to all of the properties specified by name.
 * <p/>
 * Can be used on any property registry {@code properties(...)}.
 * Example usage:
 * <pre>{@code
 *  .properties( onProperties( "rateCardType" ).enable( refreshPartial( "rateCardFilterValues" ) ) )
 * }</pre>
 *
 * @see WebUtilityConfigurers#onProperties(String...)
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Accessors(chain = true, fluent = true)
public class MultiPropertyConfigurer implements Consumer<EntityPropertyRegistryBuilder> {
    private final String[] propertyNames;
    private final List<Consumer<EntityPropertyRegistryBuilder.PropertyDescriptorBuilder>> consumers = new ArrayList<>();

    public MultiPropertyConfigurer enable(Consumer<EntityPropertyRegistryBuilder.PropertyDescriptorBuilder> consumer) {
        this.consumers.add(consumer);
        return this;
    }

    @Override
    public void accept(EntityPropertyRegistryBuilder props) {
        Stream.of(propertyNames)
                .forEach(propertyName -> {
                    EntityPropertyRegistryBuilder.PropertyDescriptorBuilder descriptorBuilder = props.property(propertyName);
                    consumers.forEach(c -> c.accept(descriptorBuilder));
                });
    }
}
