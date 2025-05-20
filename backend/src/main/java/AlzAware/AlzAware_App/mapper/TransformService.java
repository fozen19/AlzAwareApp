package AlzAware.AlzAware_App.mapper;

public interface TransformService<A, B> {
    B toDto(A source);
}
