<script>
    export let record;
</script>

<img src="/images{record.path}?id={record.id}&width=128&height=128" alt="{record.name}"/>

<style>
    img{
        margin: 16px 0;
        width: 128px;
        height: 128px;
        object-fit: cover;
    }
</style>