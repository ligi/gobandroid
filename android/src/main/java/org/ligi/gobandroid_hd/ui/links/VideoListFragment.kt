package org.ligi.gobandroid_hd.ui.links

class VideoListFragment : LinkListFragment() {
    internal override fun getData(): Array<LinkWithDescription> {
        return arrayOf(
                LinkWithDescription("https://www.youtube.com/watch?v=irGmbwqqUNs", "About the beauty of go", "surrounding game trailer"),
                LinkWithDescription("https://www.youtube.com/watch?v=bJILHweVZVw", "Introduction to the rules", "5minute from udacity"),
                LinkWithDescription("http://www.youtube.com/watch?v=gECcsSeRcNo", "Go tutorial", "3-part tutorial"),
                LinkWithDescription("https://www.youtube.com/watch?v=nwOZGr-aw9Q", "Japanology Special ", "The game of go"),
                LinkWithDescription("https://www.youtube.com/watch?v=bIQxOsRAXCo", "Deepmind vs Lee Sedol", "Video commentary of historical game")
        )
    }
}
