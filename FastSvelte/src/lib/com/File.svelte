<script>
	export let record;
	let className = '';
	export { className as class };
	let units = ['B', 'KB', 'MB', 'GB'];

    console.log(className);
	let size = () => {
		let index = 0;
		let extra = record.size;
		while (index < 4 && Math.round(extra / 1024) > 0) {
			extra = extra / 1024;
			index++;
		}
		return extra.toFixed(2) + ' ' + units[index];
	};

    let date=()=>{
        let d=new Date(record.date)
        return `${d.getMonth()+1}/${d.getDate()}/${d.getFullYear()}  ${d.getHours()}:${d.getMinutes()}`
    }
</script>

<div class={className}>
	<h3 class="content">{record.name}</h3>
	<p>时间：{date()} <span>大小：{size()}</span></p>
</div>

<style>
	p {
		color: var(--text-color);
        line-height: 1;
	}

	span {
		display: inline-block;
		margin-left: 16px;
	}
</style>
