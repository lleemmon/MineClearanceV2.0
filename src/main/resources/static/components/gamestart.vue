<template>
    <div class="gamestart">
      <br><br><br><br><br><br><br><br>
      <br><br><br><br>
      <router-link to="/game"><p>单人游戏</p></router-link>
      <router-link to="/game"><p>排行榜(研究中)</p></router-link>
      <router-link to="/game"><p>历史对局(研究中)</p></router-link>
      <button @click="doublePlay()"><p>双人排位(研究中)</p></button>
    </div>
</template>

<script>
  export default {
    name: 'gamestart',
    data(){
      return {}
    },
    methods:{
      doublePlay:function (){
        this.$emit
        (
            "sendall",
            messageType.StartBattle,
            "匹配对手中",
            true
        )
      }
    }
  }
</script>

<style scoped>
  .gamestart h1{
    text-align: center;
    color: darkorange;
  }
  .gamestart a p{
    text-align: center;
    font-size: 60px;
  }
  .gamestart{
    position: absolute;
    left:28.5%;
    top:0%;
    height: 675px;
    width: 675px;
    display: block;
    border: 1px blueviolet solid;
  }
  a:link, a:visited{
    text-decoration: none;
  }
  a:hover{
    color:orangered;
  }
</style>