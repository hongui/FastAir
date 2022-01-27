<script>
    import File from "$lib/com/File.svelte"
    let dropFiles=[];
    let deny=(e)=>{
        e.preventDefault();
    }
    let droped=(e)=>{
        e.preventDefault();
        dropFiles=e.dataTransfer.files;
        for(let f of dropFiles){
            f.date=f.lastModified;
        }
        console.log(dropFiles);
        return false;
    }
</script>

<div class="container">
    <div class="droparea" on:dragenter={deny} on:dragover={deny} on:drop={droped}>
        <p>拖拽上传~</p>
    </div>
    <div class="files">
        {#each dropFiles as file}
            <File record={file} class="item"/>
        {/each}
    </div>
</div>

<style>
    .container{
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        height: 100%;
    }
    .droparea{
        border: 1px dashed var(--primary-color);
        border-radius: 2em;
        width: 90vw;
        height: 31.4vh;
        margin: 1em;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .droparea p{
        color: var(--primary-color);
        font-weight: 500;
    }
    .files{
        width: 100%;
        height: 100%;
    }

    :global(.item){
        margin: 20px 16px;
    }
</style>