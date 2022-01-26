<script>
    export let Icon;
    export let record;

    let units=['B','KB','MB','GB']

    let size=()=>{
        let index=0;
        let extra=record.size;
        while(index<4&&Math.round(extra/1024)>0){
            extra=extra/1024;
            index++;
        }
        return extra.toFixed(2)+" " +units[index];
    }

</script>
<a href="/download{record.path}">
    <Icon style="width: 24px;
    height: 24px;
    margin: 12px;"/>
    <h3 class="content">{record.name}</h3>
    <span class="content">{size()}</span>
</a>

<style>
    a{
        display: flex;
        width: 100%;
        align-items: center;
        color: var(--primary-color);
    }

    .content{
        flex: auto;
        margin-right: 16px;
        color: var(--text-color);
    }

    h3{
        flex: auto;
    }

</style>