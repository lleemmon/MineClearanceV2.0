<template id="gameresult">
  <div class="gameresult">
    <div class="gameresultinfo">游戏结果：{{this.$route.params.gameResult}}</div>
    <div class="gameresultinfo">胜负得分:{{this.$route.params.winLoseScore}}</div>
    <div class="gameresultinfo">排雷得分:{{this.$route.params.mineScore}}</div>
    <div class="gameresultinfo">时间得分:{{this.$route.params.timeScore}}</div>
    <div class="gameresultinfo">格子得分:{{this.$route.params.boardScore }}</div>
    <div class="gameresultinfo">最终得分:{{this.$route.params.totalScore}}</div>
    <div class="gameresultinfo">个人等级分:{{this.$route.params.originalRank}} -> {{this.$route.params.newRank}}</div>
    <div class="gameresultinfo">得分浮动:{{this.$route.params.rankFloat}}</div>
    <div class="chooseinfo">
      <router-link to="/gamestart">回到主界面</router-link>
      <router-link to="/game">再来一次</router-link>
    </div>
  </div>
</template>

<style>
  .gameresult{
    position: absolute;
    left:28.5%;
    top:0%;
    height: 675px;
    width: 675px;
    display: block;
    border: 1px blueviolet solid;
  }
  .gameresultinfo{
    color: palevioletred;
    font-size: 35px;
    align-items: center;
    padding: 15px;
    padding-left: 225px;
  }
  .chooseinfo{
    margin-top: 20px;
    margin-left: -50px;
  }
  a:link, a:visited{
    text-decoration: none;
  }
  a:hover{
    color:orangered;
  }
</style>

<script>
  export default {
    data(){
      return {};
    },
    mounted() {

    }
  }
</script>

